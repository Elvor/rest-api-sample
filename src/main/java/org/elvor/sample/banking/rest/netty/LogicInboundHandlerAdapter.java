package org.elvor.sample.banking.rest.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.elvor.sample.banking.rest.HTTPMethod;
import org.elvor.sample.banking.rest.Request;
import org.elvor.sample.banking.rest.Response;
import org.elvor.sample.banking.rest.dispatcher.RequestDispatcher;
import org.elvor.sample.banking.rest.exception.ExceptionTranslator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A binding class between netty and other part of application.
 */
@Sharable
@RequiredArgsConstructor
public class LogicInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    private final ExceptionTranslator exceptionTranslator;

    private final RequestDispatcher requestDispatcher;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final FullHttpRequest request = (FullHttpRequest) msg;
        final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());

        final HTTPMethod method = HTTPMethod.valueOf(request.method().name());
        final String path = queryStringDecoder.path();
        final Response response = requestDispatcher.getHandler(path, method)
                .handle(convertRequest(request, queryStringDecoder));
        sendResponse(ctx, response);
    }

    private Request convertRequest(final FullHttpRequest request, final QueryStringDecoder queryStringDecoder) {
        final String body;
        if (request.method() == HttpMethod.POST || request.method() == HttpMethod.PUT) {
            final ByteBuf jsonBuf = request.content();
            body = jsonBuf.toString(CharsetUtil.UTF_8);
        } else {
            body = null;
        }
        final Map<String, List<String>> parameters = queryStringDecoder.parameters().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> splitParameterValues(e.getValue())
                ));
        final Map<String, String> headers = request.headers().entries().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new Request(body, parameters, headers);
    }

    private List<String> splitParameterValues(final Collection<String> parameterValues) {
        return parameterValues.stream()
                .flatMap(value -> Stream.of(value.split(",")))
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        final Response response = exceptionTranslator.translate(cause);
        sendResponse(ctx, response);
    }

    private void sendResponse(final ChannelHandlerContext ctx, final Response response) {
        final ByteBuf byteBuf = Unpooled.wrappedBuffer(response.getData().getBytes());
        final FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(response.getCode().getValue()),
                byteBuf
        );
        httpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        ctx.writeAndFlush(httpResponse);
    }
}
