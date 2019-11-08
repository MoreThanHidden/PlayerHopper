package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class PlayerHopperBlock extends HopperBlock {

    public PlayerHopperBlock() {
        super(Properties.create(Material.IRON));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader blockReader) {
        return new PlayerHopperTileEntity();
    }

}
