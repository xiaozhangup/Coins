package me.justeli.coins.hook;

import me.justeli.coins.Coins;
import me.xiaozhangup.domain.poly.Poly;
import me.xiaozhangup.domain.utils.LemonUtilsKt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class Regions {
    private static boolean isRegionEnabled = false;

    public Regions(Coins plugin) {
        initRegions();
    }

    public void initRegions() {
        isRegionEnabled = Bukkit.getPluginManager().isPluginEnabled("OrangDomain");
    }

    public boolean isRegionEnabled() {
        return isRegionEnabled;
    }

    public boolean isInRegion(Player player, String name) {
        if (!isRegionEnabled) return false;
        Poly poly = LemonUtilsKt.getPoly(player.getLocation());
        if (poly == null) return false;
        return poly.getId().equals(name);
    }

    public @Nullable Poly getRegion(Player player) {
        if (!isRegionEnabled) return null;
        return LemonUtilsKt.getPoly(player.getLocation());
    }
}
