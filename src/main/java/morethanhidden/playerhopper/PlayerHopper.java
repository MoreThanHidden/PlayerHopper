package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod(PlayerHopper.MODID)
public class PlayerHopper
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "playerhopper";

    public PlayerHopper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @ObjectHolder(MODID)
    public static class Blocks {
        static final Block playerhopper = new PlayerHopperBlock();
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
    }
}
