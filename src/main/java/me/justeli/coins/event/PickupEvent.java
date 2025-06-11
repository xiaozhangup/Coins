package me.justeli.coins.event;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/* Eli @ September 13, 2020 (creation) */
public final class PickupEvent
    extends Event
    implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Item item;
    private boolean cancelled;

    public PickupEvent(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    // -- Cancellable --

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    // -- HandlerList --

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
