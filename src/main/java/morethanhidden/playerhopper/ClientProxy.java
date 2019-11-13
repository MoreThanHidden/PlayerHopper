package morethanhidden.playerhopper;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy  {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(PlayerHopper.Items.playerhopper, 0, new ModelResourceLocation(PlayerHopper.Items.playerhopper.getRegistryName(), "inventory"));

        StateMapperBase stateMapper = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return new ModelResourceLocation(PlayerHopper.MODID + ":playerhopper", "facing=" + iBlockState.getValue(BlockHopper.FACING).getName());
            }
        };

        ModelLoader.setCustomStateMapper(PlayerHopper.Blocks.playerhopper, stateMapper);

    }


}