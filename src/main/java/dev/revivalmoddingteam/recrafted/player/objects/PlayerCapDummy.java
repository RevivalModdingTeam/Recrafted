package dev.revivalmoddingteam.recrafted.player.objects;

import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class PlayerCapDummy implements IPlayerCap {

    private final PlayerStatData statData = new PlayerStatData();

    @Override
    public PlayerStatData getStats() {
        return statData;
    }

    @Override
    public void onTick(long ticks) {

    }

    @Override
    public void syncToClient() {

    }

    @Override
    public void updateAllClients(World world) {

    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
