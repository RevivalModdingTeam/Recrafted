package dev.revivalmoddingteam.recrafted.network.client;

import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketForceChunkReload implements NetworkPacket<CPacketForceChunkReload> {

    @Override
    public void encode(CPacketForceChunkReload packet, PacketBuffer buf) {

    }

    @Override
    public CPacketForceChunkReload decode(PacketBuffer buf) {
        return new CPacketForceChunkReload();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(CPacketForceChunkReload packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().worldRenderer.loadRenderers();
        });
        ctx.get().setPacketHandled(true);
    }
}
