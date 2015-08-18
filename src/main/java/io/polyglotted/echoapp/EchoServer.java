package io.polyglotted.echoapp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.polyglotted.applauncher.server.Server;
import io.polyglotted.applauncher.settings.DefaultSettingsHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * Echoes back any received data from a client.
 */
@Slf4j
public class EchoServer implements Server {

    public static boolean started = false;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture f;
    private EchoServerSettings settings;

    public EchoServer() {
        settings = new DefaultSettingsHolder().proxy(EchoServerSettings.class);
    }

    @Override
    public void start() {
        // Configure the server.
        log.info("Echo Server starting");
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new EchoServerHandler());
                        }
                    });

            // Start the server.
            f = b.bind(settings.port()).sync();
            log.info("Echo Server started");
            started = true;
        } catch (InterruptedException e) {
            log.error("Failed to start server", e);
            stop();
        }

    }

    @Override
    public void stop() {
        // Shut down all event loops to terminate all threads.
        log.info("Echo Server stopping");
        f.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        started = false;
        log.info("Echo Server stopped");
    }

}
