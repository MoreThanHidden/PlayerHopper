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
        super(Properties.create(Material.IRON).hardnessAndResistance(3.0F));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader blockReader) {
        return new PlayerHopperTileEntity();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof PlayerHopperTileEntity){
            ((PlayerHopperTileEntity)tileentity).playerWhitelist.add(placer.getUniqueID());
            tileentity.markDirty();
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (playerIn.isCrouching()){
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote){
                if(playerIn.getHeldItemMainhand().isEmpty()){
                    if(((PlayerHopperTileEntity) tileentity).playerWhitelist.contains(playerIn.getUniqueID())){
                        ((PlayerHopperTileEntity)tileentity).playerWhitelist.remove(playerIn.getUniqueID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.removed"), Util.DUMMY_UUID);
                    }else {
                        ((PlayerHopperTileEntity) tileentity).playerWhitelist.add(playerIn.getUniqueID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.added"), Util.DUMMY_UUID);
                    }
                    tileentity.markDirty();
                }
            }
            return ActionResultType.SUCCESS;
        }

        return super.onBlockActivated(state, worldIn, pos, playerIn, hand, rayTraceResult);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        if (playerIn.isCrouching() && !playerIn.getHeldItemMainhand().isEmpty()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote) {
                String itemName = playerIn.getHeldItemMainhand().getItem().getTranslationKey();
                if (((PlayerHopperTileEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.removed.begin")
                            .append(new TranslationTextComponent(itemName))
                            .append(new TranslationTextComponent("playerhopper.item.removed.end")), Util.DUMMY_UUID);
                } else {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.added.begin")
                            .append(new TranslationTextComponent(itemName))
                            .append(new TranslationTextComponent("playerhopper.item.added.end")), Util.DUMMY_UUID);
                }
            }
        }else if(playerIn.isSneaking() && playerIn.getHeldItemMainhand().isEmpty()){
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote) {
                //Change Hopper Mode
                int currentMode = ((PlayerHopperTileEntity) tileentity).mode.ordinal();
                int newMode = currentMode == PlayerHopperMode.values().length - 1 ? 0 : currentMode + 1;
                ((PlayerHopperTileEntity) tileentity).mode = PlayerHopperMode.values()[newMode];
                playerIn.sendMessage(new TranslationTextComponent("playerhopper.modechange").appendString(" ").append(new TranslationTextComponent("playerhopper.mode." + ((PlayerHopperTileEntity) tileentity).mode.name().toLowerCase())), Util.DUMMY_UUID);
                tileentity.markDirty();
            }
        }
        super.onBlockClicked(state, worldIn, pos, playerIn);
    }

}
