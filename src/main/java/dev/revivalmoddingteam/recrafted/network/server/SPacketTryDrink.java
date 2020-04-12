package dev.revivalmoddingteam.recrafted.network.server;

import dev.revivalmoddingteam.recrafted.handler.Registry;
import dev.revivalmoddingteam.recrafted.network.NetworkPacket;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SPacketTryDrink implements NetworkPacket<SPacketTryDrink> {

    private BlockPos pos;

    public SPacketTryDrink(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void encode(SPacketTryDrink packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.pos);
    }

    @Override
    public SPacketTryDrink decode(PacketBuffer buf) {
        return new SPacketTryDrink(buf.readBlockPos());
    }

    @Override
    public void handle(SPacketTryDrink packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            IPlayerCap cap = PlayerCapFactory.get(player);
            PlayerStatData statData = cap.getStats();
            if(statData.isThirsty() && player.world.getFluidState(packet.pos).isTagged(FluidTags.WATER)) {
                statData.setThirstLevel(Math.min(20, statData.getThirstLevel() + 2));
                statData.setThirstSaturation(20.0F);
                if(player.world.rand.nextFloat() <= 0.75F) player.addPotionEffect(new EffectInstance(Registry.REffects.THIRST, 600, 0));
                cap.syncToClient();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
