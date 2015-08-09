package io.polyglotted.echoapp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.polyglotted.app.core.Gaveti;
import io.polyglotted.app.core.Overrides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Echoes back any received data from a client.
 */
public class EchoServer {

    private static final Logger logger = LoggerFactory.getLogger(EchoServer.class);

    @Inject
    private Overrides overrides;
    @Inject
    public Gaveti gaveti = null;

    public static boolean started = false;
    public static String artifactId = "";
    public static int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture f;

    public void start() {
        artifactId = gaveti.artifactId();
        port = overrides.integer("port").orSome(8007);

        // Configure the server.
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
            f = b.bind(port).sync();
            logger.info("Echo Server started");
            started = true;
        } catch (InterruptedException e) {
            logger.error("Failed to start server", e);
            stop();
        }

    }

    public void stop() {
        // Shut down all event loops to terminate all threads.
        logger.info("Echo Server stopping");
        f.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        started = false;
        logger.info("Echo Server stopped");
    }

}
