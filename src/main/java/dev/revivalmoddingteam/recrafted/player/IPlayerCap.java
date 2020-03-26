package dev.revivalmoddingteam.recrafted.player;

import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerCap extends INBTSerializable<CompoundNBT> {

    PlayerStatData getStats();

    void onTick(long ticks);

    void syncToClient();

    void updateAllClients(World world);
}
