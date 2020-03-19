package dev.revivalmoddingteam.recrafted.player.objects;

import dev.revivalmoddingteam.recrafted.common.RecraftedDamageSources;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerStatData {

    private int thirstLevel;
    private float thirstSaturation;
    private float stamina;
    private float maxStamina = 50.0F;

    public void tickPlayer(PlayerEntity player) {
        // thirst
        if(player.isSprinting()) {
            if(thirstLevel <= 6 || stamina <= 0.0F) {
                player.setSprinting(false);
            }
            thirstSaturation -= 0.02F;
        } else {
            thirstSaturation -= 0.005F;
        }
        if(thirstSaturation < 0.0F) {
            if(thirstLevel == 0) {
                if(player.world.getDifficulty().getId() >= 2 || (player.world.getDifficulty().getId() < 2 && player.getHealth() >= 2.0F)) {
                    player.attackEntityFrom(RecraftedDamageSources.THIRST, 1.0F);
                }
                thirstSaturation = 5.0F;
            } else {
                --thirstLevel;
                thirstSaturation = 20.0F;
            }
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
}
