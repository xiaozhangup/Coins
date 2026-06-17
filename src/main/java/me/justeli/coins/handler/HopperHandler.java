package me.justeli.coins.handler;

import me.justeli.coins.Coins;
import me.justeli.coins.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * @author Eli
 * @since December 13, 2016 (creation)
 */
public final class HopperHandler implements Listener {
    private final Coins coins;
    public HopperHandler(Coins coins) {
        this.coins = coins;
        coins.parseEventHandlers(this);
    }

    @EventHandler(ignoreCancelled = true)
    void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        if (!Config.DISABLE_HOPPERS) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.HOPPER) {
            return;
        }

        var item = event.getItem().getItemStack();
        if (!coins.getCoinMeta().isCoin(item)) {
            return; // no need to handle/cancel if item is not a coin
        }

        if (coins.getCoinMeta().isWithdrawnCoin(item)) {
            return; // one exemption: withdrawn coins can still be picked up
        }

        event.setCancelled(true);
    }
}
