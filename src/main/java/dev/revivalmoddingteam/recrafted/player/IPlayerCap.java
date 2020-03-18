package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.player.objects.TemperatureData;
import dev.revivalmoddingteam.recrafted.player.objects.ThirstData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerCap extends INBTSerializable<CompoundNBT> {

    ThirstData getThirstData();

    TemperatureData getTemperatureData();
}
