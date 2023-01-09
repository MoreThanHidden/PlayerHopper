package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlockEntities;
import morethanhidden.playerhopper.blocks.PlayerHopperBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(Constants.MOD_ID)
public class PlayerHopper {

    public PlayerHopper() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockEntities.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static class Blocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "playerhopper");
        public static final RegistryObject<Block> PLAYER_HOPPER = BLOCKS.register("playerhopper", () -> PlayerHopperBlocks.PLAYER_HOPPER);
    }

    public static class Items {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "playerhopper");
        static final RegistryObject<Item> PLAYER_HOPPER = ITEMS.register("playerhopper", () -> new BlockItem(PlayerHopperBlocks.PLAYER_HOPPER, new Item.Properties()));
    }

    public static class BlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "playerhopper");
        static final RegistryObject<BlockEntityType<?>> PLAYER_HOPPER = TILE_ENTITIES.register("playerhopper", () -> PlayerHopperBlockEntities.PLAYER_HOPPER);
    }

    //Register the color (3361970 / Blue) for the block (Player Hopper) and its corresponding item.
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getBlockColors().register((blockState, iEnviromentBlockReader, blockPos, i) -> 3361970, Blocks.PLAYER_HOPPER.get()));
        event.enqueueWork(() -> Minecraft.getInstance().getItemColors().register((itemStack, i) -> 3361970, Item.BY_BLOCK.get(Blocks.PLAYER_HOPPER.get())));
    }

    @SubscribeEvent
    public void creativeTabEvent(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Items.PLAYER_HOPPER.get());
        }
    }


}