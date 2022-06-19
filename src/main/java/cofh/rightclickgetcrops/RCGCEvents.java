package cofh.rightclickgetcrops;

import cofh.lib.block.IHarvestable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

@Mod.EventBusSubscriber (modid = "right_click_get_crops")
public class RCGCEvents {

    private static boolean harvestSupport = false;

    public static void init() {

        try {
            Class.forName("cofh.lib.block.IHarvestable", false, RCGCEvents.class.getClassLoader());
            harvestSupport = true;
        } catch (ClassNotFoundException cnfe) {

        }
    }

    //    @SubscribeEvent
    //    public static void handleFarmlandTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {
    //
    //        if (event.isCanceled()) {
    //            return;
    //        }
    //        if (!CoreConfig.improvedFeatherFalling) {
    //            return;
    //        }
    //        Entity entity = event.getEntity();
    //        if (entity instanceof LivingEntity) {
    //            int encFeatherFalling = getMaxEquippedEnchantmentLevel((LivingEntity) entity, FALL_PROTECTION);
    //            if (encFeatherFalling > 0) {
    //                event.setCanceled(true);
    //            }
    //        }
    //    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void handleRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {

        Level world = event.getWorld();
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // If Allow List and NOT in list, or Deny List and IS in list:
        if (RCGCConfig.allowList.get() != RCGCConfig.cropList.get().contains(ForgeRegistries.BLOCKS.getKey(block).toString())) {
            return;
        }
        Player player = event.getPlayer();
        boolean replant = RCGCConfig.replant.get();

        // IHarvestables are smart! They handle their own replanting.
        if (harvestSupport) {
            if (block instanceof IHarvestable harvestable) {
                if (harvestable.canHarvest(state)) {
                    harvestable.harvest(world, pos, state, player, replant);
                    player.swing(InteractionHand.MAIN_HAND);
                    event.setCanceled(true);
                    return;
                }
            }
        }
        boolean seedDrop = false;

        if (block instanceof CropBlock crop) {
            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);

            replant &= belowState.getBlock().canSustainPlant(belowState, world, below, Direction.UP, crop);

            if (crop.isMaxAge(state)) {
                if (!world.isClientSide) {
                    List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, null, player, player.getMainHandItem());
                    Item seedItem = crop.getCloneItemStack(world, pos, state).getItem();
                    for (ItemStack drop : drops) {
                        if (replant && !seedDrop) {
                            if (drop.getItem() == seedItem) {
                                drop.shrink(1);
                                seedDrop = true;
                            }
                        }
                        if (!drop.isEmpty()) {
                            Containers.dropItemStack(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, drop);
                        }
                    }
                    world.destroyBlock(pos, false, player);
                    if (seedDrop) {
                        world.setBlock(pos, crop.getStateForAge(0), 3);
                    }
                }
                player.swing(InteractionHand.MAIN_HAND);
                event.setCanceled(true);
            }
        } else if (block instanceof NetherWartBlock crop) {
            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);

            replant &= belowState.getBlock().canSustainPlant(belowState, world, below, Direction.UP, crop);

            if (state.getValue(NetherWartBlock.AGE) >= 3) {
                if (!world.isClientSide) {
                    List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, null, player, player.getMainHandItem());
                    Item seedItem = crop.getCloneItemStack(world, pos, state).getItem();
                    for (ItemStack drop : drops) {
                        if (replant && !seedDrop) {
                            if (drop.getItem() == seedItem) {
                                drop.shrink(1);
                                seedDrop = true;
                            }
                        }
                        if (!drop.isEmpty()) {
                            Containers.dropItemStack(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, drop);
                        }
                    }
                    world.destroyBlock(pos, false, player);
                    if (seedDrop) {
                        world.setBlock(pos, crop.defaultBlockState(), 3);
                    }
                }
                player.swing(InteractionHand.MAIN_HAND);
                event.setCanceled(true);
            }
        } else if (block instanceof CocoaBlock crop) {
            replant &= crop.canSurvive(state, world, pos);

            if (state.getValue(CocoaBlock.AGE) >= 2) {
                if (!world.isClientSide) {
                    Direction facing = state.getValue(FACING);
                    List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, null, player, player.getMainHandItem());
                    Item seedItem = crop.getCloneItemStack(world, pos, state).getItem();
                    for (ItemStack drop : drops) {
                        if (replant && !seedDrop) {
                            if (drop.getItem() == seedItem) {
                                drop.shrink(1);
                                seedDrop = true;
                            }
                        }
                        if (!drop.isEmpty()) {
                            Containers.dropItemStack(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, drop);
                        }
                    }
                    world.destroyBlock(pos, false, player);
                    if (seedDrop) {
                        world.setBlock(pos, crop.defaultBlockState().setValue(FACING, facing), 3);
                    }
                }
                player.swing(InteractionHand.MAIN_HAND);
                event.setCanceled(true);
            }
        }
    }

}
