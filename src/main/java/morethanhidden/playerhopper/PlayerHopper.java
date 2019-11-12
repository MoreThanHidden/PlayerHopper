package morethanhidden.playerhopper;

import morethanhidden.playerhopper.blocks.PlayerHopperBlock;
import morethanhidden.playerhopper.blocks.PlayerHopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = PlayerHopper.MODID)
public class PlayerHopper
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "playerhopper";


    public PlayerHopper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @GameRegistry.ObjectHolder(MODID)
    public static class Blocks {
        static final Block playerhopper = new PlayerHopperBlock();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void blockColor(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler((blockState, iEnviromentBlockReader, blockPos, i) -> 3361970, Blocks.playerhopper);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void itemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((itemStack, i) -> 3361970, Item.getItemFromBlock(Blocks.playerhopper));
    }

    @GameRegistry.ObjectHolder(MODID)
    public static class Items {
        static final Item playerhopper = new ItemBlock(Blocks.playerhopper).setRegistryName(Blocks.playerhopper.getRegistryName());
    }

    @Mod.EventBusSubscriber(modid = MODID)
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
