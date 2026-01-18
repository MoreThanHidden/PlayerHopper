package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.platform.services.Services;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PlayerHopperBlockEntities {
    public static final BlockEntityType<PlayerHopperBlockEntity> PLAYER_HOPPER = Services.PLATFORM.createBlockEntityType(PlayerHopperBlockEntity::new, PlayerHopperBlocks.PLAYER_HOPPER);
}


