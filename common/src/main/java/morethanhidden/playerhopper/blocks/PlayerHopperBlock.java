package morethanhidden.playerhopper.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

public class PlayerHopperBlock extends HopperBlock {

    public PlayerHopperBlock() {
        super(Properties.of().strength(3.0F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerHopperBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, PlayerHopperBlockEntities.PLAYER_HOPPER, PlayerHopperBlockEntity::pushItemsTick);
    }

    /**
     * Adds the player that placed the block UUID to a whitelist in the BlockEntity
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof PlayerHopperBlockEntity){
            ((PlayerHopperBlockEntity)tileentity).playerWhitelist.add(placer.getUUID());
            tileentity.setChanged();
        }
    }

    /**
     * Check if the player who interacted with the block entity is crouching, and if so,
     * check if the player's UUID is already in the playerWhitelist.
     * If it is, the UUID is removed from the list, and the player is sent a message.
     * If the UUID is not present, it is added to the list, and the player is sent a different message.
     * Then, the tileentity is marked as changed.
     * If the player was not crouching, the super method is executed.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult rayTraceResult) {
        if (playerIn.isCrouching()){
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide){
                if(playerIn.getMainHandItem().isEmpty()){
                    if(((PlayerHopperBlockEntity) tileentity).playerWhitelist.contains(playerIn.getUUID())){
                        ((PlayerHopperBlockEntity)tileentity).playerWhitelist.remove(playerIn.getUUID());
                        playerIn.sendSystemMessage(Component.translatable("playerhopper.player.removed"));
                    }else {
                        ((PlayerHopperBlockEntity) tileentity).playerWhitelist.add(playerIn.getUUID());
                        playerIn.sendSystemMessage(Component.translatable("playerhopper.player.added"));
                    }
                    tileentity.setChanged();
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, worldIn, pos, playerIn, rayTraceResult);
    }

    /**
     * This code checks to see if the player is crouching on left click, and if they have an item in their main hand.
     * If they do, it checks to see if the item is on the black list of the PlayerHopper BlockEntity.
     * If it is, it removes it from the list.
     * If it isn't, it adds it to the black list.
     * If the player is crouching but has no item in their main hand, it changes the PlayerHopper BlockEntity's mode.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) {
        if (playerIn.isCrouching() && !playerIn.getMainHandItem().isEmpty()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide) {
                String itemName = playerIn.getMainHandItem().getItem().getDescriptionId();
                if (((PlayerHopperBlockEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperBlockEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendSystemMessage(Component.translatable("playerhopper.item.removed.begin")
                            .append(Component.translatable(itemName))
                            .append(Component.translatable("playerhopper.item.removed.end")));
                } else {
                    ((PlayerHopperBlockEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendSystemMessage(Component.translatable("playerhopper.item.added.begin")
                            .append(Component.translatable(itemName))
                            .append(Component.translatable("playerhopper.item.added.end")));
                }
            }
        }else if(playerIn.isCrouching() && playerIn.getMainHandItem().isEmpty()){
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide) {
                //Change Hopper Mode
                int currentMode = ((PlayerHopperBlockEntity) tileentity).mode.ordinal();
                int newMode = currentMode == PlayerHopperMode.values().length - 1 ? 0 : currentMode + 1;
                ((PlayerHopperBlockEntity) tileentity).mode = PlayerHopperMode.values()[newMode];
                playerIn.sendSystemMessage(Component.translatable("playerhopper.modechange").append(" ").append(Component.translatable("playerhopper.mode." + ((PlayerHopperBlockEntity) tileentity).mode.name().toLowerCase())));
                tileentity.setChanged();
            }
        }
        super.attack(state, worldIn, pos, playerIn);
    }

}
