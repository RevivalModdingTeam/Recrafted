package dev.revivalmoddingteam.recrafted.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerCapStorage implements Capability.IStorage<IPlayerCap> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IPlayerCap> capability, IPlayerCap instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IPlayerCap> capability, IPlayerCap instance, Direction side, INBT nbt) {
        instance.deserializeNBT(nbt instanceof CompoundNBT ? (CompoundNBT) nbt : new CompoundNBT());
    }
}
