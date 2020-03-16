package dev.revivalmoddingteam.recrafted.network.client;

import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSyncWorldData implements NetworkPacket<CPacketSyncWorldData> {

    private final CompoundNBT nbt;

    public CPacketSyncWorldData(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    @Override
    public void encode(CPacketSyncWorldData packet, PacketBuffer buf) {
        buf.writeCompoundTag(packet.nbt);
    }

    @Override
    public CPacketSyncWorldData decode(PacketBuffer buf) {
        return new CPacketSyncWorldData(buf.readCompoundTag());
    }

    @Override
    public void handle(CPacketSyncWorldData packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().world.getCapability(WorldCapProvider.capability).orElseThrow(NullPointerException::new).deserializeNBT(packet.nbt));
        ctx.get().setPacketHandled(true);
    }
}
