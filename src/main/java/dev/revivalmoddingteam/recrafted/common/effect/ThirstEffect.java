package dev.revivalmoddingteam.recrafted.common.effect;

import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class ThirstEffect extends Effect {

    public ThirstEffect() {
        super(EffectType.HARMFUL, 0x00FFAA);
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if(entityLivingBaseIn instanceof PlayerEntity) {
            PlayerStatData statData = PlayerCapFactory.get((PlayerEntity) entityLivingBaseIn).getStats();
            statData.setThirstSaturation(statData.getThirstSaturation() - 0.01F * amplifier);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
