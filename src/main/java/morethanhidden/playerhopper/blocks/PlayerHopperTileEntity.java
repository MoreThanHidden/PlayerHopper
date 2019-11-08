package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class PlayerHopperTileEntity extends HopperTileEntity {

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;
            this.tickedGameTime = this.world.getGameTime();
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> pullItems(this));
            }

        }
    }


    public static boolean pullItems(IHopper hopper) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return !isInventoryEmpty(iinventory, direction) && IntStream.range(0, iinventory.getSizeInventory()).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction));
        } else {
            for(ItemEntity itementity : getCaptureItems(hopper)) {
                if (captureItem(hopper, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if the
     * entire stack was moved
     */
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty()) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), (Direction)null);
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
    private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
        return IntStream.range(0, inventoryIn.getSizeInventory()).allMatch(i -> inventoryIn.getStackInSlot(i).isEmpty());
    }


    @Nullable
    public static IInventory getSourceInventory(IHopper hopper) {
        if (hopper.getWorld() != null) {
            PlayerEntity player = hopper.getWorld().getClosestPlayer(hopper.getXPos(), hopper.getYPos(), hopper.getZPos());
            if(player != null && hopper.getWorld().isPlayerWithin(hopper.getXPos(), hopper.getYPos(), hopper.getZPos(), 1))
                return player.inventory;
        }
        return null;
    }

    @Override
    public TileEntityType<?> getType() {
        return PlayerHopper.PLAYER_HOPPER_TETYPE;
    }
}
