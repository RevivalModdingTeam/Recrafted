package dev.revivalmoddingteam.recrafted.world.capability;

import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketForceChunkReload;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;

public class SeasonData {

    private int currentSeasonID = 0;
    private int lastSeasonID = currentSeasonID;

    public void tickWorld(TickEvent.WorldTickEvent event) {
        int day = getDay(event.world);
        Season season = getSeason();
        int yearLength = RecraftedConfig.seasonConfig.yearCycle;
        int normalizedDay = day % yearLength;
        int seasonChange = yearLength / 12;
        this.currentSeasonID = Math.min(11, normalizedDay / seasonChange);
        if (currentSeasonID != lastSeasonID) {
            if (getSeason().updatesChunks())
                NetworkHandler.sendToAllClients(new CPacketForceChunkReload(), event.world);
            NetworkHandler.sendToAllClients(new CPacketSyncWorldData(WorldCapFactory.getData(event.world).serializeNBT()), event.world);
            Seasons.onSeasonChange(this.getSeason(), event.world);
        }
        this.lastSeasonID = currentSeasonID;
    }

    public final void setSeasonID(int id, World world) {
        int seasonDays = RecraftedConfig.seasonConfig.yearCycle / 12;
        int neededDay = id * seasonDays;
        long worldTime = neededDay * 24000L;
        for (ServerWorld sw : world.getServer().getWorlds()) {
            sw.setDayTime(worldTime);
        }
        this.currentSeasonID = id;
        NetworkHandler.sendToAllClients(new CPacketForceChunkReload(), world);
        Seasons.onSeasonChange(getSeason(), world);
    }

    public int getCurrentSeasonID() {
        return currentSeasonID;
    }

    public Season getSeason() {
        return Seasons.REGISTRY[currentSeasonID];
    }

    public int getDay(World world) {
        return (int) ((world.getDayTime() / 24000) % (RecraftedConfig.seasonConfig.yearCycle));
    }

    private int validate(int seasonID) {
        return seasonID < 0 ? Seasons.REGISTRY.length - 1 : seasonID;
    }

    public CompoundNBT write() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("seasonID", currentSeasonID);
        return nbt;
    }

    public void read(CompoundNBT nbt) {
        currentSeasonID = nbt.getInt("seasonID");
    }
}
