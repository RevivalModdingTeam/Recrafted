package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.player.objects.PlayerCapDummy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerCapProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(IPlayerCap.class)
    public static Capability<IPlayerCap> CAP = null;
    private LazyOptional<IPlayerCap> instance = LazyOptional.of(CAP::getDefaultInstance);
    protected static PlayerCapDummy capDummy = new PlayerCapDummy();

    public PlayerCapProvider() {}

    public PlayerCapProvider(PlayerEntity playerEntity) {
        this.instance = LazyOptional.of(() -> new PlayerCapFactory(playerEntity));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAP ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) CAP.getStorage().writeNBT(CAP, instance.orElseThrow(IllegalArgumentException::new), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CAP.getStorage().readNBT(CAP, instance.orElseThrow(IllegalArgumentException::new), null, nbt);
    }
}
