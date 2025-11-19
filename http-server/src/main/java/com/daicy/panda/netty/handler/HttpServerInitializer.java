/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.daicy.panda.netty.handler;

import com.daicy.panda.netty.servlet.impl.ServletContextImpl;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final boolean enableCompression;

    public HttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
        // 从ServletContext获取压缩配置
        this.enableCompression = ServletContextImpl.get().getPandaServerBuilder() != null 
                ? ServletContextImpl.get().getPandaServerBuilder().isEnableCompression() 
                : true;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec(4096, 8192, 8192, false));
        // Uncomment the following line if you don't want to handle HttpChunks.
        pipeline.addLast(new HttpObjectAggregator(100 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
//        pipeline.addLast(new HttpStaticFileServerHandler());

//        pipeline.addLast(new HttpResponseEncoder());
        // 启用响应压缩（Gzip/Deflate）
        // HttpContentCompressor会自动检测Accept-Encoding头并压缩响应
        if (enableCompression) {
            pipeline.addLast(new HttpContentCompressor());
        }
        pipeline.addLast(new IdleStateHandler(5, 0, 0));
        pipeline.addLast(new HttpServerHandler());
    }
}
