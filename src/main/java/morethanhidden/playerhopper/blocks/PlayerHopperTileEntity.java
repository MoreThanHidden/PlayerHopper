package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PlayerHopperTileEntity extends HopperTileEntity {
    List<UUID> playerWhitelist = new ArrayList<>();
    List<String> itemBlacklist = new ArrayList<>();


    @Override
    public void func_230337_a_(BlockState state,  CompoundNBT compound) {
        playerWhitelist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("whitelist_size"); i++) {
            playerWhitelist.add(compound.getUniqueId("whitelist_" + i));
        }
        itemBlacklist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("blacklist_size"); i++) {
            itemBlacklist.add(compound.getString("blacklist_" + i));
        }
        super.func_230337_a_(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("whitelist_size", playerWhitelist.size());
        for (int i = 0; i < playerWhitelist.size(); i++) {
            compound.putUniqueId("whitelist_" + i, playerWhitelist.get(i));
        }
        compound.putInt("blacklist_size", itemBlacklist.size());
        for (int i = 0; i < itemBlacklist.size(); i++) {
            compound.putString("blacklist_" + i, itemBlacklist.get(i));
        }
        return super.write(compound);
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;
            this.tickedGameTime = this.world.getGameTime();
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> pullItems(this, itemBlacklist, playerWhitelist));
            }

        }
    }

    public static boolean pullItems(IHopper hopper, List<String> itemBlacklist, List<UUID> playerWhitelist) {
        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper, playerWhitelist);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return !isInventoryEmpty(iinventory, direction) && IntStream.range(0, iinventory.getSizeInventory()).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction, itemBlacklist));
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
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction, List<String> itemBlacklist) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty() && !itemBlacklist.contains(itemstack.getItem().getTranslationKey())) {
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
    public static IInventory getSourceInventory(IHopper hopper, List<UUID> playerWhitelist) {
        if (hopper.getWorld() != null) {
            PlayerEntity player = hopper.getWorld().getClosestPlayer(hopper.getXPos(), hopper.getYPos(), hopper.getZPos(), 1, false);
            if(player != null && playerWhitelist.contains(player.getUniqueID()))
                return player.inventory;
        }
        return null;
    }

    @Override
    public TileEntityType<?> getType() {
        return PlayerHopper.PLAYER_HOPPER_TETYPE;
    }
}
