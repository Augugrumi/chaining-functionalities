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
            Charset utf8 = CharsetUtil.UTF_8;
            String message = "";
            if (msg instanceof ByteBuf)
                message = ( (ByteBuf)msg ).toString( utf8 );
            else if (msg instanceof ByteBufHolder)
                message = ( (ByteBufHolder)msg).content().toString(utf8);

            ctx.writeAndFlush(Unpooled.copiedBuffer("ACK", utf8));
            if (msg instanceof ByteBuf)
                ctx.close();

            handleMessage(message);
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
