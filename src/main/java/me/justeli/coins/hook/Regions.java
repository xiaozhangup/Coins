package me.justeli.coins.hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class Regions {
    private final Method getPoly;
    private final Method hasPermission;

    public Regions() {
        Method getPolyMethod = null;
        Method hasPermissionMethod = null;

        if (Bukkit.getPluginManager().isPluginEnabled("OrangDomain")) {
            try {
                Class<?> utilsClass = Class.forName("me.xiaozhangup.domain.utils.LemonUtilsKt");
                Class<?> polyClass = Class.forName("me.xiaozhangup.domain.poly.Poly");

                getPolyMethod = utilsClass.getMethod("getPoly", Location.class);
                hasPermissionMethod = polyClass.getMethod("hasPermission", String.class, String.class, boolean.class);
            }
            catch (ReflectiveOperationException ignored) {
            }
        }

        this.getPoly = getPolyMethod;
        this.hasPermission = hasPermissionMethod;
    }

    public boolean isDroppableRegion(Player player) {
        if (this.getPoly == null || this.hasPermission == null) {
            return true;
        }

        try {
            Object poly = this.getPoly.invoke(null, player.getLocation());
            if (poly == null) {
                return true;
            }

            Object result = this.hasPermission.invoke(poly, "drop_coin", player.getName(), true);
            return !(result instanceof Boolean allowed) || allowed;
        }
        catch (ReflectiveOperationException exception) {
            return true;
        }
    }
}
