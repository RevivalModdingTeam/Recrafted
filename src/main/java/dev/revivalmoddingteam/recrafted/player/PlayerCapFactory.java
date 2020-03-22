package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncPlayerData;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class PlayerCapFactory implements IPlayerCap {

    private final PlayerStatData playerStatData = new PlayerStatData();
    private PlayerEntity playerEntity;

    public static IPlayerCap get(PlayerEntity player) {
        return player.getCapability(PlayerCapProvider.CAP, null).orElse(PlayerCapProvider.capDummy);
    }

    public PlayerCapFactory() {
    }

    public PlayerCapFactory(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public void onTick() {
        playerStatData.tickPlayer(playerEntity);
    }

    @Override
    public PlayerStatData getStats() {
        return playerStatData;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("statData", playerStatData.write());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        playerStatData.read(nbt.getCompound("statData"));
    }

    @Override
    public void syncToClient() {
        if(playerEntity instanceof ServerPlayerEntity) {
            NetworkHandler.sendTo((ServerPlayerEntity) playerEntity, new CPacketSyncPlayerData(playerEntity.getUniqueID(), serializeNBT()));
        }
    }

    @Override
    public void updateAllClients(World world) {
        NetworkHandler.sendToAllClients(new CPacketSyncPlayerData(playerEntity.getUniqueID(), serializeNBT()), world);
    }

    @Mod.EventBusSubscriber(modid = Recrafted.MODID)
    public static class EventListener {

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            PlayerCapFactory.get(event.getPlayer()).syncToClient();
        }
    }
}
