package dev.revivalmoddingteam.recrafted.world.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldCapProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(IWorldCap.class)
    public static Capability<IWorldCap> capability = null;
    private LazyOptional<IWorldCap> instance = LazyOptional.of(capability::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == capability ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) capability.getStorage().writeNBT(capability, instance.orElseThrow(IllegalArgumentException::new), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        capability.getStorage().readNBT(capability, instance.orElseThrow(IllegalArgumentException::new), null, nbt);
    }
}
