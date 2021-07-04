package morethanhidden.playerhopper.blocks;

import morethanhidden.playerhopper.PlayerHopper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PlayerHopperTileEntity extends HopperTileEntity {
    List<UUID> playerWhitelist = new ArrayList<>();
    List<String> itemBlacklist = new ArrayList<>();
    PlayerHopperMode mode = PlayerHopperMode.INVENTORY;


    @Override
    public void load(BlockState state,  CompoundNBT compound) {
        playerWhitelist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("whitelist_size"); i++) {
            playerWhitelist.add(compound.getUUID("whitelist_" + i));
        }
        itemBlacklist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("blacklist_size"); i++) {
            itemBlacklist.add(compound.getString("blacklist_" + i));
        }
        mode = PlayerHopperMode.valueOf(compound.getString("mode"));
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("whitelist_size", playerWhitelist.size());
        for (int i = 0; i < playerWhitelist.size(); i++) {
            compound.putUUID("whitelist_" + i, playerWhitelist.get(i));
        }
        compound.putInt("blacklist_size", itemBlacklist.size());
        for (int i = 0; i < itemBlacklist.size(); i++) {
            compound.putString("blacklist_" + i, itemBlacklist.get(i));
        }
        compound.putString("mode", mode.name());
        return super.save(compound);
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            --this.cooldownTime;
            this.tickedGameTime = this.level.getGameTime();
            if (!this.isOnCooldown()) {
                this.setCooldown(0);
                this.tryMoveItems(() -> pullItems(this, itemBlacklist, playerWhitelist, mode));
            }

        }
    }

    public static boolean pullItems(IHopper hopper, List<String> itemBlacklist, List<UUID> playerWhitelist, PlayerHopperMode mode) {
        Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);
        if (ret != null) return ret;
        IInventory iinventory = getSourceInventory(hopper, playerWhitelist);
        if (iinventory instanceof PlayerInventory) {
            boolean output = false;
            Direction direction = Direction.DOWN;
            //Inventory
            if(mode.equals(PlayerHopperMode.INVENTORY) || mode.equals(PlayerHopperMode.ARMOR_HOTBAR_INVENTORY) || mode.equals(PlayerHopperMode.ARMOR_INVENTORY) || mode.equals(PlayerHopperMode.HOTBAR_INVENTORY)) {
                output = !isInventoryEmpty(iinventory, direction) && IntStream.range(9, 36).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction, itemBlacklist));
            }
            //Hotbar
            if(mode.equals(PlayerHopperMode.HOTBAR) || mode.equals(PlayerHopperMode.ARMOR_HOTBAR_INVENTORY) || mode.equals(PlayerHopperMode.HOTBAR_INVENTORY) || mode.equals(PlayerHopperMode.ARMOR_HOTBAR)){
                output = !isInventoryEmpty(iinventory, direction) && IntStream.range(0, 9).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction, itemBlacklist)) || output;
            }
            //Armor
            if(mode.equals(PlayerHopperMode.ARMOR) || mode.equals(PlayerHopperMode.ARMOR_HOTBAR_INVENTORY) || mode.equals(PlayerHopperMode.ARMOR_INVENTORY) || mode.equals(PlayerHopperMode.ARMOR_HOTBAR)){
                output = !isInventoryEmpty(iinventory, direction) && IntStream.range(36, 42).anyMatch((slot) -> pullItemFromSlot(hopper, iinventory, slot, direction, itemBlacklist)) || output;
            }
            return output;
        } else {
            for(ItemEntity itementity : getItemsAtAndAbove(hopper)) {
                if (addItem(hopper, itementity)) {
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
        ItemStack itemstack = inventoryIn.getItem(index);
        if (!itemstack.isEmpty() && !itemBlacklist.contains(itemstack.getItem().getDescriptionId())) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = addItem(inventoryIn, hopper, inventoryIn.removeItem(index, 1), (Direction)null);
            if (itemstack2.isEmpty()) {
                inventoryIn.setChanged();
                return true;
            }

            inventoryIn.setItem(index, itemstack1);
        }

        return false;
    }

    /**
     * Returns false if the specified IInventory contains any items
     */
    private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
        return IntStream.range(0, inventoryIn.getContainerSize()).allMatch(i -> inventoryIn.getItem(i).isEmpty());
    }


    @Nullable
    public static IInventory getSourceInventory(IHopper hopper, List<UUID> playerWhitelist) {
        if (hopper.getLevel() != null) {
            PlayerEntity player = hopper.getLevel().getNearestPlayer(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ(), 1, false);
            if(player != null && playerWhitelist.contains(player.getUUID()))
                return player.inventory;
        }
        return null;
    }

    @Override
    public TileEntityType<?> getType() {
        return PlayerHopper.PLAYER_HOPPER_TETYPE;
    }
}
