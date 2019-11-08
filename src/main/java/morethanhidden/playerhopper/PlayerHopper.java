package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlock;
import morethanhidden.playerhopper.blocks.PlayerHopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PlayerHopper.MODID)
public class PlayerHopper
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "playerhopper";

    @ObjectHolder("playerhopper:playerhopper")
    public static TileEntityType<PlayerHopperTileEntity> PLAYER_HOPPER_TETYPE;

    public PlayerHopper() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @ObjectHolder(MODID)
    public static class Blocks {
        static final Block playerhopper = new PlayerHopperBlock();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void blockColor(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((blockState, iEnviromentBlockReader, blockPos, i) -> 3361970, Blocks.playerhopper);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void itemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().register((itemStack, i) -> 3361970, Item.getItemFromBlock(Blocks.playerhopper));
    }

    @ObjectHolder(MODID)
    public static class Items {
        static final Item playerhopper = new BlockItem(Blocks.playerhopper, new Item.Properties()).setRegistryName(Blocks.playerhopper.getRegistryName());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            blockRegistryEvent.getRegistry().register(Blocks.playerhopper);
        }
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> blockRegistryEvent) {
            // register a new block here
            blockRegistryEvent.getRegistry().register(Items.playerhopper);
        }
        @SubscribeEvent
        public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(PlayerHopperTileEntity::new, Blocks.playerhopper).build(null).setRegistryName(Blocks.playerhopper.getRegistryName()));
        }
    }
}
