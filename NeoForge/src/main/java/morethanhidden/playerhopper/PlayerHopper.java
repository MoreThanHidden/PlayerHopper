package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlockEntities;
import morethanhidden.playerhopper.blocks.PlayerHopperBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class PlayerHopper {

    public PlayerHopper(IEventBus modEventBus) {
        modEventBus.register(this);
        Blocks.BLOCKS.register(modEventBus);
        Items.ITEMS.register(modEventBus);
        BlockEntities.TILE_ENTITIES.register(modEventBus);
    }

    public static class Blocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks( "playerhopper");
        public static final DeferredHolder<Block, Block> PLAYER_HOPPER = BLOCKS.register("playerhopper", () -> PlayerHopperBlocks.PLAYER_HOPPER);
    }

    public static class Items {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems("playerhopper");
        static final DeferredHolder<Item, Item> PLAYER_HOPPER = ITEMS.register("playerhopper", () -> new BlockItem(PlayerHopperBlocks.PLAYER_HOPPER, new Item.Properties()));
    }

    public static class BlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create( BuiltInRegistries.BLOCK_ENTITY_TYPE, "playerhopper");
        static final DeferredHolder<BlockEntityType<?>,BlockEntityType<?>> PLAYER_HOPPER = TILE_ENTITIES.register("playerhopper", () -> PlayerHopperBlockEntities.PLAYER_HOPPER);
    }

    //Register the color (3361970 / Blue) for the block (Player Hopper) and its corresponding item.
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getBlockColors().register((blockState, iEnviromentBlockReader, blockPos, i) -> 3361970, Blocks.PLAYER_HOPPER.get()));
        event.enqueueWork(() -> Minecraft.getInstance().getItemColors().register((itemStack, i) -> 3361970, Item.BY_BLOCK.get(Blocks.PLAYER_HOPPER.get())));
    }

    @SubscribeEvent
    public void creativeTabEvent(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Items.PLAYER_HOPPER.get());
        }
    }


}