package httputils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;


@ChannelHandler.Sharable
public abstract class AbsNettyMessageHandler extends ChannelInboundHandlerAdapter implements MessageHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            /*Charset utf8 = CharsetUtil.UTF_8;
            String message = "";
            if (msg instanceof ByteBuf)
                message = ( (ByteBuf)msg ).toString( utf8 );
            else if (msg instanceof ByteBufHolder)
                message = ( (ByteBufHolder)msg).content().toString(utf8);


            //handleMessage(message);

            ctx.writeAndFlush(Unpooled.copiedBuffer("ACK", utf8));
            if (msg instanceof ByteBuf) {
                ctx.close();
            }*/
            Charset utf8 = CharsetUtil.UTF_8;
            String in = "";
            if (msg instanceof ByteBuf)
                in = ( (ByteBuf)msg ).toString( utf8 );
            else if (msg instanceof ByteBufHolder)
                in = ( (ByteBufHolder)msg).content().toString(utf8);
            String out = in.toUpperCase(); // Shout!
            System.out.println(out);
            ctx.writeAndFlush( Unpooled.copiedBuffer( "HTTP/1.1 200 OK\r\n", utf8 ) );
            if (msg instanceof ByteBuf) {
                ctx.close();
            }
            String finalIn = in;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleMessage(finalIn);
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
