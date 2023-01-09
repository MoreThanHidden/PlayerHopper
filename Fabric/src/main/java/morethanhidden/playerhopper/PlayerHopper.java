package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlockEntities;
import morethanhidden.playerhopper.blocks.PlayerHopperBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class PlayerHopper implements ModInitializer {

    @Override
    public void onInitialize() {
        //Register Block
        Registry.register(BuiltInRegistries.BLOCK, "playerhopper:playerhopper", PlayerHopperBlocks.PLAYER_HOPPER);

        //Register Item
        Item PlayerHopperItem =  Registry.register(BuiltInRegistries.ITEM, "playerhopper:playerhopper", new BlockItem(PlayerHopperBlocks.PLAYER_HOPPER, new Item.Properties()));

        //Register Block Entity
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, "playerhopper:playerhopper", PlayerHopperBlockEntities.PLAYER_HOPPER);

        //Register Block Colours
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 3361970, PlayerHopperBlocks.PLAYER_HOPPER);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 3361970, PlayerHopperItem);

        //Add Creative Tab
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.addAfter(Items.HOPPER, PlayerHopperItem);
        });
    }
}