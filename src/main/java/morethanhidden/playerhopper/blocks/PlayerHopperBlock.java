package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.Block;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.material.Material;

public class PlayerHopperBlock extends HopperBlock {
    public PlayerHopperBlock() {
        super(Properties.create(Material.IRON));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }
}
