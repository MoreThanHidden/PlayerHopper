package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.BlockHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PlayerHopperBlock extends BlockHopper {

    public PlayerHopperBlock() {
        super();
        setRegistryName(PlayerHopper.MODID, "playerhopper");
        setUnlocalizedName("playerhopper");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new PlayerHopperTileEntity();
    }

}
