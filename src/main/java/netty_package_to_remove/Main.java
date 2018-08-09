package netty_package_to_remove;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

public class Main {
    public static void main( String[] args ) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        int port = 55630;
        ChannelInitializer init = new Init();

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

    private static class Init extends ChannelInitializer
    {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast( new MessageHandler() );
        }
    }

    private static class MessageHandler extends ChannelInboundHandlerAdapter
    {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            try {
                Charset utf8 = CharsetUtil.UTF_8;
                String in = "";
                if (msg instanceof ByteBuf)
                    in = ( (ByteBuf)msg ).toString( utf8 );
                else if (msg instanceof ByteBufHolder)
                    in = ( (ByteBufHolder)msg).content().toString(utf8);
                String out = in.toUpperCase(); // Shout!
                System.out.println(out);
                ctx.writeAndFlush( Unpooled.copiedBuffer( out, utf8 ) );
                if (msg instanceof ByteBuf)
                    ctx.close();
            }
            finally {
                ReferenceCountUtil.release( msg );
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
