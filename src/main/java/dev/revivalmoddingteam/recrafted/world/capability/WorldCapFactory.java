package dev.revivalmoddingteam.recrafted.world.capability;

import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

public class WorldCapFactory implements IWorldCap {

    private SeasonData seasonData = new SeasonData();

    @Override
    public void tickWorld(TickEvent.WorldTickEvent event) {
        seasonData.tickWorld(event);
    }

    @Override
    public SeasonData getSeasonData() {
        return seasonData;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("seasonData", seasonData.write());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        seasonData.read(nbt.contains("seasonData") ? nbt.getCompound("seasonData") : new CompoundNBT());
    }

    public static IWorldCap getData(World world) {
        return world.getCapability(WorldCapProvider.capability).orElseThrow(() -> new NullPointerException("Couldn't get world capability"));
    }

    @Override
    public void updateClients(World world) {
        NetworkHandler.sendToAllClients(new CPacketSyncWorldData(this.serializeNBT()), world);
    }
}
