package de.gui3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * Applies a virtual GUI scale only to enabled vanilla container windows.
 * Minecraft's global GUI scale and the in-game HUD are left untouched.
 */
public final class Gui3Scale {
    private Gui3Scale() {
    }

    public static boolean isContainer(Object receiver) {
        return receiver instanceof AbstractContainerScreen<?>;
    }

    public static boolean shouldScale(Object receiver) {
        if (!isContainer(receiver)) {
            return false;
        }

        Gui3Config config = Gui3Config.get();
        if (!config.enabled) {
            return false;
        }

        boolean inventory = receiver instanceof InventoryScreen
                || receiver instanceof CreativeModeInventoryScreen;

        boolean storage = receiver instanceof ContainerScreen
                || receiver instanceof ShulkerBoxScreen
                || receiver instanceof HopperScreen
                || receiver instanceof DispenserScreen
                || receiver instanceof CrafterScreen;

        boolean workstation = receiver instanceof AnvilScreen
                || receiver instanceof BeaconScreen
                || receiver instanceof BlastFurnaceScreen
                || receiver instanceof BrewingStandScreen
                || receiver instanceof CartographyTableScreen
                || receiver instanceof CraftingScreen
                || receiver instanceof EnchantmentScreen
                || receiver instanceof FurnaceScreen
                || receiver instanceof GrindstoneScreen
                || receiver instanceof LoomScreen
                || receiver instanceof MerchantScreen
                || receiver instanceof SmithingScreen
                || receiver instanceof SmokerScreen
                || receiver instanceof StonecutterScreen;

        boolean recognized = inventory || storage || workstation;

        if (inventory && config.playerInventory) return true;
        if (storage && config.storageContainers) return true;
        if (workstation && config.workstations) return true;
        return !recognized && config.otherContainers;
    }

    /**
     * Convert the game's current global logical GUI scale into the requested
     * logical scale for this screen only.
     */
    public static double factor(Object receiver) {
        if (!shouldScale(receiver)) {
            return 1.0;
        }

        Minecraft client = Minecraft.getInstance();
        double currentScale = client.getWindow().getGuiScale();
        double target = Gui3Config.get().targetScale;
        if (currentScale <= 0.0 || target <= 0.0) {
            return 1.0;
        }
        return currentScale / target;
    }

    public static double offsetX(Object receiver) {
        return shouldScale(receiver) ? Gui3Config.get().offsetX : 0.0;
    }

    public static double offsetY(Object receiver) {
        return shouldScale(receiver) ? Gui3Config.get().offsetY : 0.0;
    }

    private static Screen screen(Object receiver) {
        if (receiver instanceof Screen screen && shouldScale(receiver)) {
            return screen;
        }
        return null;
    }

    public static double mapX(Object receiver, double x) {
        Screen screen = screen(receiver);
        if (screen == null) return x;

        double scale = factor(receiver);
        double center = screen.width * 0.5;
        return center + (x - center - offsetX(receiver)) / scale;
    }

    public static double mapY(Object receiver, double y) {
        Screen screen = screen(receiver);
        if (screen == null) return y;

        double scale = factor(receiver);
        double center = screen.height * 0.5;
        return center + (y - center - offsetY(receiver)) / scale;
    }

    public static double mapDelta(Object receiver, double delta) {
        return shouldScale(receiver) ? delta / factor(receiver) : delta;
    }

    public static MouseButtonEvent mapEvent(Object receiver, MouseButtonEvent event) {
        if (!shouldScale(receiver)) return event;
        return new MouseButtonEvent(
                mapX(receiver, event.x()),
                mapY(receiver, event.y()),
                event.buttonInfo()
        );
    }
}
