package morethanhidden.playerhopper.blocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerHopperBlocks {

    // Create RegistryKeys
    public static ResourceKey<Block> BLOCK_KEY = ResourceKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath("playerhopper", "playerhopper"));
    public static ResourceKey<Item> ITEM_KEY = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath("playerhopper", "playerhopper"));

    public static final Block PLAYER_HOPPER = new PlayerHopperBlock(BLOCK_KEY);
}
