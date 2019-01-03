package org.elvor.sample;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elvor.sample.banking.App;
import org.elvor.sample.banking.model.Account;
import org.elvor.sample.banking.service.vo.TransferInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
public class ApiIT {

    private static final String URL = "http://localhost:8080/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Object lock = new Object();


    @Test
    public void test() throws IOException, ClassNotFoundException {

        final Thread thread = new Thread(() -> {
            new App().run(() -> {
                synchronized (lock) {
                    lock.notifyAll();
                }
            });
        });
        thread.start();
        System.out.println("test");
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                thread.interrupt();
            }
            testAll();
            thread.interrupt();
        }
    }

    private void testAll() throws IOException, ClassNotFoundException {

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            final String firstOwner = "owner1";
            final long firstAccountMoney = 1000L;
            final String secondOwner = "owner2";
            final long secondAccountMoney = 2000L;

            testCreate(client, firstAccountMoney, firstOwner);
            final Long firstId = testGet(client, firstOwner, firstAccountMoney);

            testCreate(client, secondAccountMoney, secondOwner);
            final Long secondId = testGet(client, secondOwner, secondAccountMoney);


            final long amount = 500L;

            testTransfer(client, firstId, secondId, amount);

            testGet(client, firstOwner, firstAccountMoney - amount);
            testGet(client, secondOwner, secondAccountMoney + amount);

            testDelete(client, firstId);
            testDelete(client, secondId);

            testGetDeleted(client, firstId);
            testGetDeleted(client, secondId);
        }

    }

    private void testCreate(final HttpClient httpClient, final long money, final String ownerName) throws IOException {
        final Account account = new Account();
        account.setMoney(money);
        account.setOwnerName(ownerName);

        final HttpPost request = new HttpPost(URL);
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(account)));
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        execute(httpClient, request, response -> {
            Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
            return null;
        });
    }

    private Long testGet(final HttpClient httpClient, final String ownerName,
                         final long money) throws IOException {
        return testGet(
                httpClient,
                account -> {
                    if (ownerName.equals(account.getOwnerName())) {
                        Assert.assertNotNull(account.getId());
                        Assert.assertEquals(money, account.getMoney());
                        return true;
                    }
                    return false;
                }
        );
    }

    private Long testGetDeleted(final HttpClient httpClient, final long id) throws IOException, ClassNotFoundException {
        final HttpGet request = new HttpGet(URL + id);


        return execute(httpClient, request, response -> {
            Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
            return null;
        });
    }


    private Long testGet(
            final HttpClient httpClient, final Predicate<Account> predicate) throws IOException {
        final HttpGet request = new HttpGet(URL);


        return execute(httpClient, request, response -> {
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            Long foundId = null;

            try {
                final Class<?> clz = Class.forName(Account.class.getCanonicalName());
                final JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clz);
                final List<Account> list = objectMapper.readValue(getResponseContent(response), type);
                for (Account account : list) {
                    if (predicate.test(account)) {
                        foundId = account.getId();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                Assert.fail("Wrong test configuration");
            }
            if (foundId == null) {
                Assert.fail("Entity wasn't created");
            }
            return foundId;
        });
    }

    private void testTransfer(
            final HttpClient client, final long from, final long to, final long amount) throws IOException {
        final TransferInfo transferInfo = new TransferInfo();
        transferInfo.setFrom(from);
        transferInfo.setTo(to);
        transferInfo.setAmount(amount);

        final HttpPost request = new HttpPost(URL + "transfer");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(transferInfo)));
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");


        execute(client, request, response -> {
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            return null;
        });
    }

    private String getResponseContent(final HttpResponse response) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(response.getEntity().getContent(), writer, StandardCharsets.UTF_8);
            return writer.toString();
        }
    }

    private void testDelete(final HttpClient client, final long id) throws IOException {
        final HttpDelete request = new HttpDelete(URL + "/" + id);

        execute(client, request, response -> {
            Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
            return null;
        });
    }

    private <T> T execute(
            final HttpClient client, final HttpUriRequest request,
        final Function<HttpResponse, T> call) throws IOException {
        HttpResponse response = null;
        try {
            response = client.execute(request);
            return call.apply(response);
        }
        finally {
            if (response != null && response.getEntity() != null && response.getEntity().getContent() != null) {
                response.getEntity().getContent().close();
            }
        }
    }
}
