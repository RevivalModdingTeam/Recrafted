package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerCapFactory implements IPlayerCap {

    private final PlayerStatData playerStatData = new PlayerStatData();
    private PlayerEntity playerEntity;

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
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public void syncToClient() {

    }
}
