package dev.revivalmoddingteam.recrafted.common.items;

import dev.revivalmoddingteam.recrafted.common.ItemGroups;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class RecraftedFood extends RecraftedItem {

    private final Stats stats;

    public RecraftedFood(String key, Stats stats) {
        super(key, new Properties().group(ItemGroups.RECRAFTED_ITEMS));
        this.stats = stats;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isRemote) return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
        if(stats.food > 0) {
            if(playerIn.canEat(stats.alwaysUseable)) {
                playerIn.setActiveHand(handIn);
            } else return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
        }
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return stats.food > 0 ? UseAction.EAT : UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return stats.fastUse ? 16 : 32;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if(!worldIn.isRemote && entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            IPlayerCap cap = PlayerCapFactory.get(player);
            FoodStats foodStats = player.getFoodStats();
            PlayerStatData statData = cap.getStats();
            foodStats.setFoodLevel(Math.min(20, foodStats.getFoodLevel() + stats.food));
            foodStats.setFoodSaturationLevel(Math.min(24.0F, foodStats.getSaturationLevel() + stats.foodSaturation));
            statData.setThirstLevel(Math.min(20, statData.getThirstLevel() + stats.thirstLevel));
            statData.setThirstSaturation(stats.thirstLevel > 0 ? 25.0F : statData.getThirstSaturation());
            statData.setStamina(Math.min(statData.getMaxStamina(), statData.getStamina() + stats.energy));
            stats.applyEffects(player);
            cap.syncToClient();
            stack.shrink(1);
        }
        return stack;
    }

    public static class Stats {

        private int food = 0;
        private float foodSaturation = 0.0F;
        private int thirstLevel = 0;
        private float energy = 0.0F;
        private boolean fastUse = false;
        private boolean alwaysUseable = false;
        private List<Pair<Integer, Supplier<EffectInstance>>> extraEffects;

        public Stats() {
        }

        public Stats food(int level, float saturation) {
            this.food = level;
            this.foodSaturation = saturation;
            return this;
        }

        public Stats water(int level) {
            this.thirstLevel = level;
            return this;
        }

        public Stats energy(float energy) {
            this.energy = energy;
            return this;
        }

        public Stats setQuick() {
            this.fastUse = true;
            return this;
        }

        public Stats alwaysUseable() {
            this.alwaysUseable = true;
            return this;
        }

        public Stats effect(int chance, Supplier<EffectInstance> effectFactory) {
            if(extraEffects == null) extraEffects = new ArrayList<>();
            extraEffects.add(Pair.of(chance, effectFactory));
            return this;
        }

        public int getFood() {
            return food;
        }

        public float getFoodSaturation() {
            return foodSaturation;
        }

        public int getThirstLevel() {
            return thirstLevel;
        }

        public float getEnergy() {
            return energy;
        }

        public boolean isFastUse() {
            return fastUse;
        }

        public boolean isAlwaysUseable() {
            return alwaysUseable;
        }

        public boolean hasPotionEffects() {
            return extraEffects != null && !extraEffects.isEmpty();
        }

        public void applyEffects(LivingEntity entity) {
            if(!hasPotionEffects()) return;
            Random random = new Random();
            for(Pair<Integer, Supplier<EffectInstance>> pair : extraEffects) {
                if(random.nextFloat() <= pair.getLeft() / 100f) {
                    entity.addPotionEffect(pair.getRight().get());
                }
            }
        }
    }
}
