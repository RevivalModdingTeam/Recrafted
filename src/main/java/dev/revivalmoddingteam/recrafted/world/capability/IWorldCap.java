package dev.revivalmoddingteam.recrafted.world.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;

public interface IWorldCap extends INBTSerializable<CompoundNBT> {

    SeasonData getSeasonData();

    // listeners
    void tickWorld(TickEvent.WorldTickEvent event);

    void updateClients(World world);
}
