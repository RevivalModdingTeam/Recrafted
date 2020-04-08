package dev.revivalmoddingteam.recrafted.world.capability;

import dev.revivalmoddingteam.recrafted.config.RecraftedConfig;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketForceChunkReload;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.world.season.Season;
import dev.revivalmoddingteam.recrafted.world.season.Seasons;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

public class SeasonData {

    private int currentSeasonID = 0;
    private int dayInCycle = 0;
    private int lastTickDay;

    public void tickWorld(TickEvent.WorldTickEvent event) {
        lastTickDay = getDay(event.world);
        Season season = getSeason();
        if(lastTickDay % RecraftedConfig.seasonConfig.yearCycle == season.mildSeasonEnd) {
            if(currentSeasonID == 3) {
                lastTickDay = 0;
                currentSeasonID = 0;
            } else {
                currentSeasonID = currentSeasonID + 1;
            }
            NetworkHandler.sendToAllClients(new CPacketForceChunkReload(), event.world);
            NetworkHandler.sendToAllClients(new CPacketSyncWorldData(WorldCapFactory.getData(event.world).serializeNBT()), event.world);
            Seasons.onSeasonChange(this.getSeason(), event.world);
        }
    }

    public final void setSeasonID(int id, World world) {
        int seasonDays = RecraftedConfig.seasonConfig.yearCycle / 4;
        this.dayInCycle = seasonDays * id;
        this.lastTickDay = dayInCycle;
        this.currentSeasonID = id;
        NetworkHandler.sendToAllClients(new CPacketForceChunkReload(), world);
    }

    public void setDayInCycle(int dayInCycle) {
        this.dayInCycle = dayInCycle;
    }

    public int getCurrentSeasonID() {
        return currentSeasonID;
    }

    public int getDayInCycle() {
        return dayInCycle;
    }

    public Season getSeason() {
        return Seasons.REGISTRY[currentSeasonID];
    }

    public int getDay(World world) {
        return dayInCycle + (int) ((world.getDayTime() / 24000) % (RecraftedConfig.seasonConfig.yearCycle));
    }

    public CompoundNBT write() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("day", lastTickDay);
        nbt.putInt("seasonID", currentSeasonID);
        return nbt;
    }

    public void read(CompoundNBT nbt) {
        dayInCycle = nbt.getInt("day");
        currentSeasonID = nbt.getInt("seasonID");
    }
}
