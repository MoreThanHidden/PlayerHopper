package morethanhidden.playerhopper.blocks;

import net.minecraft.block.BlockHopper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PlayerHopperTileEntity extends TileEntityHopper {
    List<UUID> playerWhitelist = new ArrayList<>();
    List<String> itemBlacklist = new ArrayList<>();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        playerWhitelist = new ArrayList<>();
        for (int i = 0; i < compound.getInteger("whitelist_size"); i++) {
            playerWhitelist.add(compound.getUniqueId("whitelist_" + i));
        }
        itemBlacklist = new ArrayList<>();
        for (int i = 0; i < compound.getInteger("blacklist_size"); i++) {
            itemBlacklist.add(compound.getString("blacklist_" + i));
        }
        super.readFromNBT(compound);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("whitelist_size", playerWhitelist.size());
        for (int i = 0; i < playerWhitelist.size(); i++) {
            compound.setUniqueId("whitelist_" + i, playerWhitelist.get(i));
        }
        compound.setInteger("blacklist_size", itemBlacklist.size());
        for (int i = 0; i < itemBlacklist.size(); i++) {
            compound.setString("blacklist_" + i, itemBlacklist.get(i));
        }
        return super.writeToNBT(compound);
    }

    @Override
    protected boolean updateHopper() {
        if (this.world != null && !this.world.isRemote){
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())){
                boolean flag = false;

                if (!this.isInventoryEmpty()){
                    flag = this.transferItemsOut();
                }

                if (!this.isFull()){
                    flag = pullItems(this, playerWhitelist) || flag;
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

    private static boolean pullItems(IHopper hopper, List<UUID> playerWhitelist) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper, playerWhitelist);
        if (iinventory != null) {
            return !isInventoryEmpty(iinventory) && IntStream.range(0, iinventory.getSizeInventory()).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot));
        } else {
            for(EntityItem itementity : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(), hopper.getZPos())) {
                if (captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static boolean captureItem(IInventory inventory, EntityItem item) {
        boolean flag = false;
        ItemStack itemstack = item.getItem().copy();
        ItemStack itemstack1 = putStackInInventoryAllSlots(null, inventory, itemstack, null);
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
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty()) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), null);
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
    private static boolean isInventoryEmpty(IInventory inventoryIn) {
        return IntStream.range(0, inventoryIn.getSizeInventory()).allMatch(i -> inventoryIn.getStackInSlot(i).isEmpty());
    }


    private static IInventory getSourceInventory(IHopper hopper, List<UUID> playerWhitelist) {
            EntityPlayer player = hopper.getWorld().getClosestPlayer(hopper.getXPos(), hopper.getYPos(), hopper.getZPos(), 1, false);
            if(player != null && playerWhitelist.contains(player.getUniqueID()))
                return player.inventory;
        return null;
    }

}
