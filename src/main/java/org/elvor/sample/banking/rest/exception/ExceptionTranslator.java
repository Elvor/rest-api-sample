package org.elvor.sample.banking.rest.exception;

import io.netty.channel.ChannelHandlerContext;

public interface ExceptionTranslator {
    void handle(Throwable throwable, ChannelHandlerContext channelHandlerContext);
}
