package dev.revivalmoddingteam.recrafted.network.client;

import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketUpdateBiomePrecipitation implements NetworkPacket<CPacketUpdateBiomePrecipitation> {

    @Override
    public void encode(CPacketUpdateBiomePrecipitation packet, PacketBuffer buf) {

    }

    @Override
    public CPacketUpdateBiomePrecipitation decode(PacketBuffer buf) {
        return new CPacketUpdateBiomePrecipitation();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handle(CPacketUpdateBiomePrecipitation packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Seasons.onSeasonChange(WorldCapFactory.getData(Minecraft.getInstance().world).getSeasonData().getSeason(), Minecraft.getInstance().world));
        ctx.get().setPacketHandled(true);
    }
}
