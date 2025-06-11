package me.justeli.coins.handler;

import me.justeli.coins.Coins;
import me.justeli.coins.config.Config;
import me.justeli.coins.event.PickupEvent;
import me.justeli.coins.util.PermissionNode;
import me.justeli.coins.util.Util;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PickupHandler
    implements Listener {
    private final Coins coins;
    private final Set<UUID> thrownCoinCache = new HashSet<>();
    private final HashMap<UUID, Double> pickupAmountCache = new HashMap<>();
    private final HashMap<UUID, Long> pickupTimeCache = new HashMap<>();

    public PickupHandler(Coins coins) {
        this.coins = coins;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        // handled properly with PickupEvent
        if (event.getEntity() instanceof Player)
            return;

        // don't let mobs pick up coins that are already being given to players
        if (!this.thrownCoinCache.contains(event.getItem().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(PickupEvent event) {
        if (Util.isDisabledHere(event.getPlayer().getWorld()))
            return;

        Item item = event.getItem();
        if (!this.coins.getCoinUtil().isCoin(item.getItemStack()))
            return;

        Player player = event.getPlayer();
        event.setCancelled(true);

        if (!player.hasPermission(PermissionNode.DISABLE) || player.isOp() || player.hasPermission("*")) {
            double amount = this.coins.getCoinUtil().getValue(item.getItemStack());
            giveCoin(item, player, amount);
        }
    }

    private void giveCoin(Item item, Player player, double randomMoney) {
        if (this.thrownCoinCache.contains(item.getUniqueId()))
            return;

        this.thrownCoinCache.add(item.getUniqueId());
        item.setVelocity(new Vector(0, 0.4, 0));

        this.coins.sync(5, () ->
        {
            item.remove();
            this.thrownCoinCache.remove(item.getUniqueId());
        });

        // pass 0 for random amount
        if (randomMoney == 0) {
            giveRandomMoney(item.getItemStack(), player);
        } else {
            giveMoney(player, randomMoney);
        }

        if (Config.PICKUP_SOUND) {
            Util.playCoinPickupSound(player);
        }
    }

    public void giveRandomMoney(ItemStack item, Player player) {
        if (Config.DROP_EACH_COIN) {
            giveMoney(player, item.getAmount());
        } else {
            int amount = item.getAmount();
            double total = amount * Util.getRandomMoneyAmount() * this.coins.getCoinUtil().getIncrement(item);

            giveMoney(player, total);
        }
    }

    public void giveMoney(Player player, double rawAmount) {
        final double amount = Util.round(rawAmount);
        this.coins.economy().deposit(player.getUniqueId(), amount, () ->
        {
            UUID uniqueId = player.getUniqueId();
            long previousTime = this.pickupTimeCache.computeIfAbsent(uniqueId, empty -> 0L);

            if (previousTime > System.currentTimeMillis() - 1500) {
                // recently shown actionbar
                double previousAmount = this.pickupAmountCache.computeIfAbsent(uniqueId, empty -> 0D);
                this.pickupAmountCache.put(uniqueId, amount + previousAmount);
            } else {
                this.pickupAmountCache.put(uniqueId, amount);
            }

            final double displayAmount = this.pickupAmountCache.computeIfAbsent(uniqueId, empty -> 0D);
            if (!Config.PICKUP_MESSAGE.isEmpty()) {
                Util.send(Config.PICKUP_MESSAGE_POSITION, player, Config.PICKUP_MESSAGE, displayAmount);
            }

            this.pickupTimeCache.put(uniqueId, System.currentTimeMillis());
        });
    }
}
