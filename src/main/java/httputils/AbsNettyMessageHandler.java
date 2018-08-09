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

/**
 * Abstract handler to manage messages coming from server bootstrapped with Netty
 */
@ChannelHandler.Sharable
public abstract class AbsNettyMessageHandler extends ChannelInboundHandlerAdapter implements MessageHandler {

    /**
     * Listen to the channel to retrieve messagges
     * @param ctx ChannelHandlerContext channel to be listened
     * @param msg Object message received
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            Charset utf8 = CharsetUtil.UTF_8;
            String message = "";
            if (msg instanceof ByteBuf) // TCP packet
                message = ( (ByteBuf)msg ).toString( utf8 );
            else if (msg instanceof ByteBufHolder) // UDP packet
                message = ( (ByteBufHolder)msg).content().toString(utf8);

            // response
            ctx.writeAndFlush(Unpooled.copiedBuffer(MyHttpConstants.OK, utf8));
            if (msg instanceof ByteBuf) {
                ctx.close();
            }

            // handling the message in a separate thread
            String finalIn = message;
            new Thread(() -> handleMessage(finalIn)).start();

        }
        finally {
            ReferenceCountUtil.release( msg );
        }
    }

    /**
     * Error handling
     * @param ctx ChannelHandlerContext channel to be listened
     * @param cause Throwable exception launched
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
