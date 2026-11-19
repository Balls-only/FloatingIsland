package com.floatingisland;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Client-to-server request. The server remains authoritative for world edits.
 */
public record ToggleIslandPayload(boolean enabled, int size) implements CustomPayload {
    public static final Id<ToggleIslandPayload> ID =
            new Id<>(Identifier.of(FloatingIslandMod.MOD_ID, "toggle_island"));

    public static final PacketCodec<RegistryByteBuf, ToggleIslandPayload> CODEC =
            PacketCodec.of(
                    (payload, buf) -> {
                        buf.writeBoolean(payload.enabled());
                        buf.writeVarInt(payload.size());
                    },
                    buf -> new ToggleIslandPayload(buf.readBoolean(), buf.readVarInt())
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
