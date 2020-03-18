package dev.revivalmoddingteam.recrafted.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static dev.revivalmoddingteam.recrafted.handler.Registry.REntityTypes.RECRAFTED_ITEM;

public class RecraftedItemEntity extends Entity {

    private ItemStack stack;

    public RecraftedItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public RecraftedItemEntity(World world, double x, double y, double z, ItemStack stack) {
        this(RECRAFTED_ITEM.get(), world);
        setPosition(x, y, z);
        this.stack = stack;
    }

    @Override
    public void tick() {
        Vec3d motion = getMotion();
        this.setMotion(motion.x, motion.y - 0.01, motion.z);
        super.tick();
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("item", stack.serializeNBT());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        stack = ItemStack.read(compound.getCompound("item"));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
