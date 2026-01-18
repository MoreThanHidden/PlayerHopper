package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlockEntities;
import morethanhidden.playerhopper.blocks.PlayerHopperBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class PlayerHopper implements ModInitializer {

    @Override
    public void onInitialize() {
        //Register Block
        Registry.register(BuiltInRegistries.BLOCK, PlayerHopperBlocks.BLOCK_KEY, PlayerHopperBlocks.PLAYER_HOPPER);

        //Register Item
        Item PlayerHopperItem =  Registry.register(BuiltInRegistries.ITEM, PlayerHopperBlocks.ITEM_KEY, new BlockItem(PlayerHopperBlocks.PLAYER_HOPPER, new Item.Properties().setId(PlayerHopperBlocks.ITEM_KEY)));

        //Register Block Entity
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "playerhopper"), PlayerHopperBlockEntities.PLAYER_HOPPER);

        //Register Block Colours
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 3361970, PlayerHopperBlocks.PLAYER_HOPPER);

        //Add Creative Tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.addAfter(Items.HOPPER, PlayerHopperItem);
        });
    }
}