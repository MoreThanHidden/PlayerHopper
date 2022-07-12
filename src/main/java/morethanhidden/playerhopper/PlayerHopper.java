package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlock;
import morethanhidden.playerhopper.blocks.PlayerHopperBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PlayerHopper.MODID)
public class PlayerHopper
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "playerhopper";

    public PlayerHopper() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockEntityTypes.BLOCK_ENTITYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static class Blocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "playerhopper");
        static final RegistryObject<Block> PLAYER_HOPPER = BLOCKS.register("playerhopper", PlayerHopperBlock::new);
    }

    public static class Items {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "playerhopper");
        static final RegistryObject<Item> PLAYER_HOPPER = ITEMS.register("playerhopper", () -> new BlockItem(Blocks.PLAYER_HOPPER.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
    }

    public static class BlockEntityTypes {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITYS = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "playerhopper");
        public static final RegistryObject<BlockEntityType<PlayerHopperBlockEntity>> PLAYER_HOPPER = BLOCK_ENTITYS.register("playerhopper", () -> BlockEntityType.Builder.of(PlayerHopperBlockEntity::new, Blocks.PLAYER_HOPPER.get()).build(null));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetupEvent(FMLClientSetupEvent event) {
      event.enqueueWork(() -> Minecraft.getInstance().getBlockColors().register((blockState, iEnviromentBlockReader, blockPos, i) -> 3361970, Blocks.PLAYER_HOPPER.get()));
      event.enqueueWork(() -> Minecraft.getInstance().getItemColors().register((itemStack, i) -> 3361970, Item.BY_BLOCK.get(Blocks.PLAYER_HOPPER.get())));
    }


}
