package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PlayerHopperBlock extends HopperBlock {

    public PlayerHopperBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(3.0F));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader blockReader) {
        return new PlayerHopperTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof PlayerHopperTileEntity){
            ((PlayerHopperTileEntity)tileentity).playerWhitelist.add(placer.getUniqueID());
            tileentity.markDirty();
        }
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (playerIn.isCrouching()){
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote){
                if(playerIn.getHeldItemMainhand().isEmpty()){
                    if(((PlayerHopperTileEntity) tileentity).playerWhitelist.contains(playerIn.getUniqueID())){
                        ((PlayerHopperTileEntity)tileentity).playerWhitelist.remove(playerIn.getUniqueID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.removed"));
                    }else {
                        ((PlayerHopperTileEntity) tileentity).playerWhitelist.add(playerIn.getUniqueID());
                        playerIn.sendMessage(new TranslationTextComponent("playerhopper.player.added"));
                    }
                    tileentity.markDirty();
                }
            }
            return ActionResultType.SUCCESS;
        }

        return super.func_225533_a_(state, worldIn, pos, playerIn, hand, rayTraceResult);
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
        if (playerIn.isCrouching() && !playerIn.getHeldItemMainhand().isEmpty()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote) {
                String itemName = playerIn.getHeldItemMainhand().getItem().getTranslationKey();
                if (((PlayerHopperTileEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.removed.begin")
                            .appendSibling(new TranslationTextComponent(itemName))
                            .appendSibling(new TranslationTextComponent("playerhopper.item.removed.end")));
                } else {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendMessage(new TranslationTextComponent("playerhopper.item.added.begin")
                            .appendSibling(new TranslationTextComponent(itemName))
                            .appendSibling(new TranslationTextComponent("playerhopper.item.added.end")));
                }
            }
        }
        super.onBlockClicked(state, worldIn, pos, playerIn);
    }
}
