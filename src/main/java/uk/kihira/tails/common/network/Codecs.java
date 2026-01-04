package uk.kihira.tails.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public final class Codecs
{
    public static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeLong(value.getMostSignificantBits());
                buf.writeLong(value.getLeastSignificantBits());
            },
            buf -> new UUID(buf.readLong(), buf.readLong())
    );
}
