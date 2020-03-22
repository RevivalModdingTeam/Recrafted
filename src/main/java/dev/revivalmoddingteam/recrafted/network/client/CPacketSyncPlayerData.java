package dev.revivalmoddingteam.recrafted.network.client;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CPacketSyncPlayerData implements NetworkPacket<CPacketSyncPlayerData> {

    private UUID player;
    private CompoundNBT nbt;

    public CPacketSyncPlayerData() {}

    public CPacketSyncPlayerData(UUID player, CompoundNBT nbt) {
        this.player = player;
        this.nbt = nbt;
    }

    @Override
    public void encode(CPacketSyncPlayerData packet, PacketBuffer buf) {
        buf.writeUniqueId(packet.player);
        buf.writeCompoundTag(packet.nbt);
    }

    @Override
    public CPacketSyncPlayerData decode(PacketBuffer buf) {
        return new CPacketSyncPlayerData(buf.readUniqueId(), buf.readCompoundTag());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(CPacketSyncPlayerData packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = mc.world.getPlayerByUuid(packet.player);
            if(player == null) {
                Recrafted.log.fatal("Couldn't find player with id {}, data sync unsuccessful!", packet.player);
                return;
            }
            PlayerCapFactory.get(player).deserializeNBT(packet.nbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
