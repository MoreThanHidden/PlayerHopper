package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class PlayerHopperBlock extends Block {
    public PlayerHopperBlock() {
        super(Properties.create(Material.IRON));
        setRegistryName(PlayerHopper.MODID, "playerhopper");
    }
}
