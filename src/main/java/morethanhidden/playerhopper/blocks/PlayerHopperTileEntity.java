package morethanhidden.playerhopper.blocks;

import net.minecraft.block.BlockHopper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class PlayerHopperTileEntity extends TileEntityHopper {

    @Override
    protected boolean updateHopper() {
        if (this.world != null && !this.world.isRemote){
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())){
                boolean flag = false;

                if (!this.isInventoryEmpty()){
                    flag = this.transferItemsOut();
                }

                if (!this.isFull()){
                    flag = pullItems(this) || flag;
                }

                if (flag){
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    public static boolean pullItems(IHopper hopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper);
        if (iinventory != null) {
            EnumFacing direction = EnumFacing.DOWN;
            return !isInventoryEmpty(iinventory, direction) && IntStream.range(0, iinventory.getSizeInventory()).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction));
        } else {
            for(EntityItem itementity : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(), hopper.getZPos())) {
                if (captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean captureItem(IInventory inventory, EntityItem item) {
        boolean flag = false;
        ItemStack itemstack = item.getItem().copy();
        ItemStack itemstack1 = putStackInInventoryAllSlots((IInventory)null, inventory, itemstack, (EnumFacing) null);
        if (itemstack1.isEmpty()) {
            flag = true;
            item.setItem(ItemStack.EMPTY);
        } else {
            item.setItem(itemstack1);
        }
        return flag;
    }

    /**
     * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if the
     * entire stack was moved
     */
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty()) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing) null);
            if (itemstack2.isEmpty()) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }

    /**
     * Returns false if the specified IInventory contains any items
     */
    private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
        return IntStream.range(0, inventoryIn.getSizeInventory()).allMatch(i -> inventoryIn.getStackInSlot(i).isEmpty());
    }


    @Nullable
    public static IInventory getSourceInventory(IHopper hopper) {
            EntityPlayer player = hopper.getWorld().getClosestPlayer(hopper.getXPos(), hopper.getYPos(), hopper.getZPos(), 1, false);
            if(player != null)
                return player.inventory;
        return null;
    }

}
