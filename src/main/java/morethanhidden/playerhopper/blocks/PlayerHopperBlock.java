package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;

public class PlayerHopperBlock extends HopperBlock {

    public PlayerHopperBlock() {
        super(Properties.of(Material.METAL).strength(3.0F));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new PlayerHopperTileEntity();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof PlayerHopperTileEntity){
            ((PlayerHopperTileEntity)tileentity).playerWhitelist.add(placer.getUUID());
            tileentity.setChanged();
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (playerIn.isCrouching()){
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isClientSide){
                if(playerIn.getMainHandItem().isEmpty()){
                    if(((PlayerHopperTileEntity) tileentity).playerWhitelist.contains(playerIn.getUUID())){
                        ((PlayerHopperTileEntity)tileentity).playerWhitelist.remove(playerIn.getUUID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.removed"), Util.NIL_UUID);
                    }else {
                        ((PlayerHopperTileEntity) tileentity).playerWhitelist.add(playerIn.getUUID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.added"), Util.NIL_UUID);
                    }
                    tileentity.setChanged();
                }
            }
            return ActionResultType.SUCCESS;
        }

        return super.use(state, worldIn, pos, playerIn, hand, rayTraceResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        if (playerIn.isCrouching() && !playerIn.getMainHandItem().isEmpty()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isClientSide) {
                String itemName = playerIn.getMainHandItem().getItem().getDescriptionId();
                if (((PlayerHopperTileEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.removed.begin")
                            .append(new TranslationTextComponent(itemName))
                            .append(new TranslationTextComponent("playerhopper.item.removed.end")), Util.NIL_UUID);
                } else {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.added.begin")
                            .append(new TranslationTextComponent(itemName))
                            .append(new TranslationTextComponent("playerhopper.item.added.end")), Util.NIL_UUID);
                }
            }
        }else if(playerIn.isCrouching() && playerIn.getMainHandItem().isEmpty()){
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isClientSide) {
                //Change Hopper Mode
                int currentMode = ((PlayerHopperTileEntity) tileentity).mode.ordinal();
                int newMode = currentMode == PlayerHopperMode.values().length - 1 ? 0 : currentMode + 1;
                ((PlayerHopperTileEntity) tileentity).mode = PlayerHopperMode.values()[newMode];
                playerIn.sendMessage(new TranslationTextComponent("playerhopper.modechange").append(" ").append(new TranslationTextComponent("playerhopper.mode." + ((PlayerHopperTileEntity) tileentity).mode.name().toLowerCase())), Util.NIL_UUID);
                tileentity.setChanged();
            }
        }
        super.attack(state, worldIn, pos, playerIn);
    }

}
