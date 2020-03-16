package dev.revivalmoddingteam.recrafted.world.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class WorldCapStorage implements Capability.IStorage<IWorldCap> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IWorldCap> capability, IWorldCap instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IWorldCap> capability, IWorldCap instance, Direction side, INBT nbt) {
        instance.deserializeNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
    }
}
