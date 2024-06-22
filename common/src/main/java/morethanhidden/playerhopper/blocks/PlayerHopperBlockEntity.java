package morethanhidden.playerhopper.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class PlayerHopperBlockEntity extends HopperBlockEntity {
    List<UUID> playerWhitelist = new ArrayList<>();
    List<String> itemBlacklist = new ArrayList<>();
    PlayerHopperMode mode = PlayerHopperMode.INVENTORY;

    public PlayerHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        playerWhitelist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("whitelist_size"); i++) {
            playerWhitelist.add(compound.getUUID("whitelist_" + i));
        }
        itemBlacklist = new ArrayList<>();
        for (int i = 0; i < compound.getInt("blacklist_size"); i++) {
            itemBlacklist.add(compound.getString("blacklist_" + i));
        }
        mode = PlayerHopperMode.valueOf(compound.getString("mode"));
        super.loadAdditional(compound, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("whitelist_size", playerWhitelist.size());
        for (int i = 0; i < playerWhitelist.size(); i++) {
            compound.putUUID("whitelist_" + i, playerWhitelist.get(i));
        }
        compound.putInt("blacklist_size", itemBlacklist.size());
        for (int i = 0; i < itemBlacklist.size(); i++) {
            compound.putString("blacklist_" + i, itemBlacklist.get(i));
        }
        compound.putString("mode", mode.name());
    }


    /**
     * This method is used to push items from a HopperBlockEntity to another inventory.
     * It sets the cooldown of the blockEntity to 0 and calls the tryMoveItems and pullItems methods to
     * try and move items between inventories if not on cooldown following the Player UUID Whitelist and Item Blacklist.
     */
    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, HopperBlockEntity blockEntity) {
        if (!level.isClientSide) {
            --blockEntity.cooldownTime;
            blockEntity.tickedGameTime = level.getGameTime();
            if (!blockEntity.isOnCooldown()) {
                blockEntity.setCooldown(0);
                tryMoveItems(level, pos, state, blockEntity, () -> pullItems(level,blockEntity, ((PlayerHopperBlockEntity)blockEntity).itemBlacklist, ((PlayerHopperBlockEntity)blockEntity).playerWhitelist, ((PlayerHopperBlockEntity)blockEntity).mode));
            }
        }
    }

    /**
     * Checks the mode specified, either inventory, hotbar, or armor, and checks for items in those slots.
     * If any of the slots in the mode specified contain items it will attempt to add them to the hopper.
     * If the inventory is not empty it adds the items and returns true, otherwise it will check for items above
     * the hopper and attempt to add them.
     */
    public static boolean pullItems(Level world, Hopper hopper, List<String> itemBlacklist, List<UUID> playerWhitelist, PlayerHopperMode mode) {
        Container iinventory = getSourceInventory(world, hopper, playerWhitelist);
        if (iinventory instanceof Inventory) {
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
            for(ItemEntity itementity : getItemsAtAndAbove(world, hopper)) {
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
    private static boolean pullItemFromSlot(Hopper hopper, Container inventoryIn, int index, Direction direction, List<String> itemBlacklist) {
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
    private static boolean isInventoryEmpty(Container inventoryIn, Direction side) {
        return IntStream.range(0, inventoryIn.getContainerSize()).allMatch(i -> inventoryIn.getItem(i).isEmpty());
    }


    public static Container getSourceInventory(Level world, Hopper hopper, List<UUID> playerWhitelist) {
        if (world != null) {
            Player player = world.getNearestPlayer(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ(), 1, false);
            if(player != null && playerWhitelist.contains(player.getUUID()))
                return player.getInventory();
        }
        return null;
    }

    @Override
    public BlockEntityType<?> getType() {
        return PlayerHopperBlockEntities.PLAYER_HOPPER;
    }
}
