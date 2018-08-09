package httputils;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Simple both UDP and TCP server using Netty library
 */
public class NettyTCPAndUDPServer extends AbsBaseServer {
    /**
     * Method to wait for a connection and manage the message
     * @param port int that represent the port on which the sever is listening
     * @param handler AbsNettyMessageHandler to which requests are retrieved
     */
    @Override
    public void receive(int port, MessageHandler handler) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelInitializer init = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast((AbsNettyMessageHandler)handler);
            }
        };

        new Thread(() -> {
            try {
                new Bootstrap()
                        .group(bossGroup)
                        .channel(NioDatagramChannel.class)
                        .handler(init)
                        .bind(port).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            new ServerBootstrap()
                    .group( bossGroup, workerGroup )
                    .channel( NioServerSocketChannel.class )
                    .childHandler(init)
                    .bind( port ).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
