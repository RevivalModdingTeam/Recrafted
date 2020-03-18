package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.player.objects.TemperatureData;
import dev.revivalmoddingteam.recrafted.player.objects.ThirstData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerCapFactory implements IPlayerCap {

    private final ThirstData thirstData = new ThirstData();
    private final TemperatureData temperatureData = new TemperatureData();

    private PlayerEntity playerEntity;

    public PlayerCapFactory() {
    }

    public PlayerCapFactory(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public ThirstData getThirstData() {
        return thirstData;
    }

    @Override
    public TemperatureData getTemperatureData() {
        return temperatureData;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
