package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Random;

public class PlayerHopperBlock extends HopperBlock {

    public PlayerHopperBlock() {
        super(Properties.of(Material.METAL).strength(3.0F));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerHopperBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, PlayerHopper.PLAYERHOPPER_TYPE, PlayerHopperBlockEntity::pushItemsTick);
    }

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

    @SuppressWarnings("NullableProblems")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (playerIn.isCrouching()){
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide){
                if(playerIn.getMainHandItem().isEmpty()){
                    if(((PlayerHopperBlockEntity) tileentity).playerWhitelist.contains(playerIn.getUUID())){
                        ((PlayerHopperBlockEntity)tileentity).playerWhitelist.remove(playerIn.getUUID());
                        playerIn.sendMessage(new TranslatableComponent("playerhopper.player.removed"), Util.NIL_UUID);
                    }else {
                        ((PlayerHopperBlockEntity) tileentity).playerWhitelist.add(playerIn.getUUID());
                        playerIn.sendMessage(new TranslatableComponent("playerhopper.player.added"), Util.NIL_UUID);
                    }
                    tileentity.setChanged();
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.use(state, worldIn, pos, playerIn, hand, rayTraceResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) {
        if (playerIn.isCrouching() && !playerIn.getMainHandItem().isEmpty()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide) {
                String itemName = playerIn.getMainHandItem().getItem().getDescriptionId();
                if (((PlayerHopperBlockEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperBlockEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendMessage(new TranslatableComponent("playerhopper.item.removed.begin")
                            .append(new TranslatableComponent(itemName))
                            .append(new TranslatableComponent("playerhopper.item.removed.end")), Util.NIL_UUID);
                } else {
                    ((PlayerHopperBlockEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendMessage(new TranslatableComponent("playerhopper.item.added.begin")
                            .append(new TranslatableComponent(itemName))
                            .append(new TranslatableComponent("playerhopper.item.added.end")), Util.NIL_UUID);
                }
            }
        }else if(playerIn.isCrouching() && playerIn.getMainHandItem().isEmpty()){
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperBlockEntity && !worldIn.isClientSide) {
                //Change Hopper Mode
                int currentMode = ((PlayerHopperBlockEntity) tileentity).mode.ordinal();
                int newMode = currentMode == PlayerHopperMode.values().length - 1 ? 0 : currentMode + 1;
                ((PlayerHopperBlockEntity) tileentity).mode = PlayerHopperMode.values()[newMode];
                playerIn.sendMessage(new TranslatableComponent("playerhopper.modechange").append(" ").append(new TranslatableComponent("playerhopper.mode." + ((PlayerHopperBlockEntity) tileentity).mode.name().toLowerCase())), Util.NIL_UUID);
                tileentity.setChanged();
            }
        }
        super.attack(state, worldIn, pos, playerIn);
    }

}
