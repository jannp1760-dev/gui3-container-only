package de.gui3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Client-only settings persisted in config/gui3container.json. */
public final class Gui3Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("gui3container.json");

    private static Gui3Config INSTANCE = new Gui3Config();

    public boolean enabled = true;
    public double targetScale = 3.0;
    public double offsetX = 0.0;
    public double offsetY = 0.0;

    public boolean playerInventory = true;
    public boolean storageContainers = true;
    public boolean workstations = true;
    public boolean otherContainers = true;

    private Gui3Config() {
    }

    public static Gui3Config get() {
        return INSTANCE;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Gui3Config loaded = GSON.fromJson(Files.readString(FILE), Gui3Config.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                }
            }
        } catch (Exception exception) {
            System.err.println("[GUI3] Could not load settings: " + exception.getMessage());
            INSTANCE = new Gui3Config();
        }
        INSTANCE.clamp();
    }

    public void save() {
        clamp();
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException exception) {
            System.err.println("[GUI3] Could not save settings: " + exception.getMessage());
        }
    }

    public void reset() {
        enabled = true;
        targetScale = 3.0;
        offsetX = 0.0;
        offsetY = 0.0;
        playerInventory = true;
        storageContainers = true;
        workstations = true;
        otherContainers = true;
        save();
    }

    private void clamp() {
        targetScale = Math.max(1.0, Math.min(6.0, targetScale));
        offsetX = Math.max(-100.0, Math.min(100.0, offsetX));
        offsetY = Math.max(-100.0, Math.min(100.0, offsetY));
    }
}
