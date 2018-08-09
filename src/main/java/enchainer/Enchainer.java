package enchainer;

import executeonmain.ExecuteOnMain;
import httputils.AbsBaseServer;
import httputils.AbsNettyMessageHandler;
import httputils.NettyTCPAndUDPServer;
import httputils.Server;
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
import netty_package_to_remove.Main;
import vfn.AbsBaseVNF;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import static httputils.MessageWrapper.wrapMessage;

public class Enchainer implements ExecuteOnMain {

    /**
     * Logging utility field
     */
    private static final Logger LOGGER = Logger.getLogger(Enchainer.class.getName());

    private int port;

    private static String[] chain;

    private NettyTCPAndUDPServer server;

    public Enchainer(int port, String[] chain) {
        this.port = port;
        this.chain = chain;
        server = new NettyTCPAndUDPServer();
    }

    @Override
    public void execute() {
        /*server.receive(port, new AbsNettyMessageHandler() {
            @Override
            public void handleMessage(String message) {
                String jsonMessage = wrapMessage(message, chain);
                LOGGER.warning(jsonMessage);
                new AbsBaseServer().sendPOST(jsonMessage, chain[0]);
            }
        });*/
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelInitializer init = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(new Init());
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
                String finalIn = in;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new AbsBaseServer().sendPOST(wrapMessage(finalIn, chain), chain[0]);
                    }
                }).start();

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

    public static void main(String[] args) {
        String[] chain = Arrays.copyOfRange(args, 1, args.length);
        new Enchainer(Integer.parseInt(args[0]), chain).execute();
    }

}
