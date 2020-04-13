package dev.revivalmoddingteam.recrafted.handler.event.common;

import dev.revivalmoddingteam.recrafted.Recrafted;
import dev.revivalmoddingteam.recrafted.api.loader.drink.DrinkData;
import dev.revivalmoddingteam.recrafted.api.loader.drink.DrinkManager;
import dev.revivalmoddingteam.recrafted.common.command.RecraftedCommand;
import dev.revivalmoddingteam.recrafted.common.entity.RecraftedItemEntity;
import dev.revivalmoddingteam.recrafted.handler.event.Action;
import dev.revivalmoddingteam.recrafted.network.NetworkHandler;
import dev.revivalmoddingteam.recrafted.network.client.CPacketSyncWorldData;
import dev.revivalmoddingteam.recrafted.network.server.SPacketTryDrink;
import dev.revivalmoddingteam.recrafted.player.IPlayerCap;
import dev.revivalmoddingteam.recrafted.player.PlayerCapFactory;
import dev.revivalmoddingteam.recrafted.player.PlayerCapProvider;
import dev.revivalmoddingteam.recrafted.player.objects.PlayerStatData;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapFactory;
import dev.revivalmoddingteam.recrafted.world.capability.WorldCapProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Recrafted.MODID)
public class CommonForgeEventHandler {

    public static long tickCounter;

    // TODO realistic item entities
    //@SubscribeEvent
    public static void entitySpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof ItemEntity) {
            if(entity.world.isRemote) return;
            RecraftedItemEntity recraftedItemEntity = new RecraftedItemEntity(entity.world, entity.posX, entity.posY, entity.posZ, ((ItemEntity) entity).getItem().copy());
            recraftedItemEntity.setMotion(entity.getMotion());
            event.getWorld().addEntity(recraftedItemEntity);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        if(world.isRemote && event.getHand() == Hand.MAIN_HAND && player.getHeldItemMainhand().isEmpty() && player.isSneaking()) {
            IPlayerCap playerCap = PlayerCapFactory.get(player);
            PlayerStatData statData = playerCap.getStats();
            if(statData.isThirsty()) {
                BlockPos pos = event.getPos().offset(event.getFace());
                if(world.getFluidState(pos).isTagged(FluidTags.WATER)) {
                    player.swingArm(event.getHand());
                    player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
                    NetworkHandler.sendServerPacket(new SPacketTryDrink(pos));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        if(PotionUtils.getEffectsFromStack(stack).isEmpty()) {
            DrinkData drinkData = DrinkManager.getDrinkStats(stack.getItem());
            if(drinkData != null) {
                drinkData.applyOn(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        NetworkHandler.sendTo(player, new CPacketSyncWorldData(WorldCapFactory.getData(player.world).serializeNBT()));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        ++tickCounter;
        WorldCapFactory.getData(event.world).tickWorld(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            updateScheduler();
            IPlayerCap cap = PlayerCapFactory.get(event.player);
            cap.onTick(tickCounter);
        }
    }

    @SubscribeEvent
    public static void attachWorldCap(AttachCapabilitiesEvent<World> event) {
        event.addCapability(Recrafted.makeResource("worldcap"), new WorldCapProvider());
    }

    @SubscribeEvent
    public static void attachPlayerCap(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            event.addCapability(Recrafted.makeResource("playercap"), new PlayerCapProvider((PlayerEntity) event.getObject()));
        }
    }

    @SubscribeEvent
    public static void registerCommands(FMLServerStartingEvent event) {
        RecraftedCommand.register(event.getCommandDispatcher());
    }

    private static final List<Action> scheduler = new ArrayList<>();

    public static void schedule(int afterTicks, Runnable runnable) {
        scheduler.add(new Action(afterTicks, runnable));
    }

    private static void updateScheduler() {
        scheduler.removeIf(Action::tick);
    }
}
