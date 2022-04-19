package cofh.rightclickgetcrops;

import cofh.lib.block.IHarvestable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber (modid = "right_click_get_crops")
public class RCGCEvents {

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

        World world = event.getWorld();
        if (event.getHand() == Hand.OFF_HAND) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // If Allow List and NOT in list, or Deny List and IS in list:
        if (RCGCConfig.allowList.get() != RCGCConfig.cropList.get().contains(block.getRegistryName().toString())) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        boolean replant = RCGCConfig.replant.get();

        // IHarvestables are smart! They handle their own replanting.
        if (block instanceof IHarvestable) {
            IHarvestable harvestable = (IHarvestable) block;
            if (harvestable.canHarvest(state)) {
                harvestable.harvest(world, pos, state, player, replant);
                player.swing(Hand.MAIN_HAND);
                event.setCanceled(true);
            }
        } else if (block instanceof CropsBlock) {
            CropsBlock crop = (CropsBlock) block;
            boolean seedDrop = false;

            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);
            Block belowBlock = belowState.getBlock();

            replant &= belowBlock.canSustainPlant(belowState, world, below, Direction.UP, crop);

            if (crop.isMaxAge(state)) {
                if (!world.isClientSide) {
                    List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, pos, null, player, player.getMainHandItem());
                    Item seedItem = crop.getCloneItemStack(world, pos, state).getItem();
                    for (ItemStack drop : drops) {
                        if (replant && !seedDrop) {
                            if (drop.getItem() == seedItem) {
                                drop.shrink(1);
                                seedDrop = true;
                            }
                        }
                        if (!drop.isEmpty()) {
                            InventoryHelper.dropItemStack(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, drop);
                        }
                    }
                    world.destroyBlock(pos, false, player);
                    if (seedDrop) {
                        world.setBlock(pos, crop.getStateForAge(0), 3);
                    }
                }
                player.swing(Hand.MAIN_HAND);
                event.setCanceled(true);
            }
        } else if (block instanceof NetherWartBlock) {
            NetherWartBlock crop = (NetherWartBlock) block;
            boolean seedDrop = false;

            BlockPos below = pos.below();
            BlockState belowState = world.getBlockState(below);
            Block belowBlock = belowState.getBlock();

            replant &= belowBlock.canSustainPlant(belowState, world, below, Direction.UP, crop);

            if (state.getValue(NetherWartBlock.AGE) >= 3) {
                if (!world.isClientSide) {
                    List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, pos, null, player, player.getMainHandItem());
                    Item seedItem = crop.getCloneItemStack(world, pos, state).getItem();
                    for (ItemStack drop : drops) {
                        if (replant && !seedDrop) {
                            if (drop.getItem() == seedItem) {
                                drop.shrink(1);
                                seedDrop = true;
                            }
                        }
                        if (!drop.isEmpty()) {
                            InventoryHelper.dropItemStack(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, drop);
                        }
                    }
                    world.destroyBlock(pos, false, player);
                    if (seedDrop) {
                        world.setBlock(pos, crop.defaultBlockState(), 3);
                    }
                }
                player.swing(Hand.MAIN_HAND);
                event.setCanceled(true);
            }
        }
    }

}
