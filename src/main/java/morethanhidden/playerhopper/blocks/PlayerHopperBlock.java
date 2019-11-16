package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayerHopperBlock extends BlockHopper {

    private static final Map<EnumFacing, List<AxisAlignedBB>> AABB_MAP = new HashMap<EnumFacing, List<AxisAlignedBB>>() {
        {
            final AxisAlignedBB[] hopperBox = new AxisAlignedBB[]{
                    new AxisAlignedBB(0, 10 / 16F, 0, 1, 1, 1),
                    new AxisAlignedBB(4 / 16F, 4 / 16F, 4 / 16F, 12 / 16F, 10 / 16F, 12 / 16F)
            };
            put(EnumFacing.DOWN, Arrays.asList(hopperBox[0], hopperBox[1],
                    new AxisAlignedBB(6 / 16F, 0, 6 / 16F, 10 / 16F, 4 / 16F, 10 / 16F)));
            put(EnumFacing.NORTH, Arrays.asList(hopperBox[0], hopperBox[1],
                    new AxisAlignedBB(6 / 16F, 4 / 16F, 0, 10 / 16F, 8 / 16F, 4 / 16F)));
            put(EnumFacing.SOUTH, Arrays.asList(hopperBox[0], hopperBox[1],
                    new AxisAlignedBB(6 / 16F, 4 / 16F, 12 / 16F, 10 / 16F, 8 / 16F, 1)));
            put(EnumFacing.EAST, Arrays.asList(hopperBox[0], hopperBox[1],
                    new AxisAlignedBB(0, 4 / 16F, 6 / 16F, 4 / 16F, 8 / 16F, 10 / 16F)));
            put(EnumFacing.WEST, Arrays.asList(hopperBox[0], hopperBox[1],
                    new AxisAlignedBB(12 / 16F, 4 / 16F, 6 / 16F, 1, 8 / 16F, 10 / 16F)));
        }
    };

    public PlayerHopperBlock() {
        super();
        setRegistryName(PlayerHopper.MODID, "playerhopper");
        setUnlocalizedName("playerhopper");
        setCreativeTab(CreativeTabs.REDSTONE);
        setSoundType(SoundType.METAL);
        setResistance(8.0F);
        setHardness(3.0F);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        /*
        TODO: find out why this clears the hopper's inventory.
        boolean flag = !worldIn.isBlockPowered(pos);
        if (flag != state.getValue(ENABLED)){
            worldIn.setBlockState(pos, state.withProperty(ENABLED, flag), 4);
        }*/
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof PlayerHopperTileEntity){
            ((PlayerHopperTileEntity)tileentity).playerWhitelist.add(placer.getUniqueID());
            tileentity.markDirty();
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        return AABB_MAP.get(blockState.getValue(BlockHopper.FACING)).stream().map(s -> rayTrace(pos, start, end, s)).anyMatch(Objects::nonNull) ? super.collisionRayTrace(blockState, worldIn, pos, start, end) : null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking()){
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote){
                if(playerIn.getHeldItemMainhand().isEmpty()){
                    if(((PlayerHopperTileEntity) tileentity).playerWhitelist.contains(playerIn.getUniqueID())){
                        ((PlayerHopperTileEntity)tileentity).playerWhitelist.remove(playerIn.getUniqueID());
                        playerIn.sendMessage(new TextComponentTranslation("playerhopper.player.removed"));
                    }else {
                        ((PlayerHopperTileEntity) tileentity).playerWhitelist.add(playerIn.getUniqueID());
                        playerIn.sendMessage(new TextComponentTranslation("playerhopper.player.added"));
                    }
                    tileentity.markDirty();
                }
            }
            return true;
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (playerIn.isSneaking() && !playerIn.getHeldItemMainhand().isEmpty()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof PlayerHopperTileEntity && !worldIn.isRemote) {
                String itemName = playerIn.getHeldItemMainhand().getItem().getUnlocalizedName();
                if (((PlayerHopperTileEntity) tileentity).itemBlacklist.contains(itemName)) {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.remove(itemName);
                    playerIn.sendMessage(new TextComponentTranslation("playerhopper.item.removed.begin")
                            .appendText(" ")
                            .appendSibling(new TextComponentTranslation(itemName + ".name"))
                            .appendSibling(new TextComponentTranslation("playerhopper.item.removed.end")));
                } else {
                    ((PlayerHopperTileEntity) tileentity).itemBlacklist.add(itemName);
                    playerIn.sendMessage(new TextComponentTranslation("playerhopper.item.added.begin")
                            .appendText(" ")
                            .appendSibling(new TextComponentTranslation(itemName + ".name"))
                            .appendSibling(new TextComponentTranslation("playerhopper.item.added.end")));
                }
            }
        }
        super.onBlockClicked(worldIn, pos, playerIn);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new PlayerHopperTileEntity();
    }

}
