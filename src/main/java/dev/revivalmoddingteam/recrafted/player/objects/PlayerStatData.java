package dev.revivalmoddingteam.recrafted.player.objects;

import dev.revivalmoddingteam.recrafted.common.RecraftedDamageSources;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PlayerStatData {

    private int thirstLevel = 20;
    private float thirstSaturation = 50.0F;
    private float stamina = 50.0F;
    private float maxStamina = 50.0F;

    public void tickPlayer(PlayerEntity player) {
        // thirst
        if(player.world.isRemote) return;
        if(player.isSprinting()) {
            if(thirstLevel <= 6 || stamina <= 0.0F) {
                player.setSprinting(false);
            }
            thirstSaturation -= 0.02F;
        } else {
            thirstSaturation -= 0.005F;
        }
        if(thirstSaturation < 0.0F) {
            if(!player.world.isRemote && thirstLevel == 0) {
                if(player.world.getDifficulty().getId() >= 2 || (player.world.getDifficulty().getId() < 2 && player.getHealth() >= 2.0F)) {
                    player.attackEntityFrom(RecraftedDamageSources.THIRST, 1.0F);
                }
                thirstSaturation = 1.0F;
            } else {
                --thirstLevel;
                thirstSaturation = 20.0F;
            }
            PlayerCapFactory.get(player).syncToClient();
        }
        // TODO stamina
    }

    public void setThirstLevel(int thirstLevel) {
        this.thirstLevel = thirstLevel;
    }

    public void setThirstSaturation(float thirsSaturation) {
        this.thirstSaturation = thirsSaturation;
    }

    public void setStamina(float stamina) {
        this.stamina = stamina;
    }

    public int getThirstLevel() {
        return thirstLevel;
    }

    public float getThirstSaturation() {
        return thirstSaturation;
    }

    public float getStamina() {
        return stamina;
    }

    public CompoundNBT write() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("thirstLevel", thirstLevel);
        nbt.putFloat("thirstSaturation", thirstSaturation);
        nbt.putFloat("stamina", stamina);
        nbt.putFloat("staminaLimit", maxStamina);
        return nbt;
    }

    public void read(CompoundNBT nbt) {
        thirstLevel = nbt.getInt("thirstLevel");
        thirstSaturation = nbt.getFloat("thirstSaturation");
        stamina = nbt.getFloat("stamina");
        maxStamina = nbt.getFloat("maxStamina");
    }
}
