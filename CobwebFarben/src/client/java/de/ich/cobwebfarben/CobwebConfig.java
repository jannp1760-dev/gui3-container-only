package de.ich.cobwebfarben;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class CobwebConfig {
    private static final String DEFAULT_MAIN = "F4F4F4";
    private static final String DEFAULT_OUTLINE = "202020";
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("cobwebfarben.properties");

    private static String mainHex = DEFAULT_MAIN;
    private static String outlineHex = DEFAULT_OUTLINE;
    private static boolean outlineEnabled = true;
    private static int outlineWidth = 1;

    private CobwebConfig() { }

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            properties.load(reader);
            mainHex = normalizeHex(properties.getProperty("main", DEFAULT_MAIN));
            String outline = properties.getProperty("outline", DEFAULT_OUTLINE);
            outlineEnabled = !outline.equalsIgnoreCase("off");
            outlineHex = outlineEnabled ? normalizeHex(outline) : DEFAULT_OUTLINE;
            outlineWidth = Math.max(1, Math.min(3, Integer.parseInt(properties.getProperty("outlineWidth", "1"))));
        } catch (IOException | IllegalArgumentException exception) {
            mainHex = DEFAULT_MAIN;
            outlineHex = DEFAULT_OUTLINE;
            outlineEnabled = true;
            outlineWidth = 1;
        }
    }

    public static void setMainColor(String value) {
        mainHex = normalizeHex(value);
        save();
    }

    public static void setOutlineColor(String value) {
        if (value.equalsIgnoreCase("off") || value.equalsIgnoreCase("aus")) {
            outlineEnabled = false;
        } else {
            outlineHex = normalizeHex(value);
            outlineEnabled = true;
        }
        save();
    }

    public static void setOutlineWidth(int value) {
        outlineWidth = Math.max(1, Math.min(3, value));
        save();
    }

    public static void reset() {
        mainHex = DEFAULT_MAIN;
        outlineHex = DEFAULT_OUTLINE;
        outlineEnabled = true;
        outlineWidth = 1;
        save();
    }

    public static int mainRgb() {
        return Integer.parseInt(mainHex, 16);
    }

    public static int outlineRgb() {
        return Integer.parseInt(outlineHex, 16);
    }

    public static String mainHex() { return mainHex; }
    public static String outlineHex() { return outlineHex; }
    public static boolean outlineEnabled() { return outlineEnabled; }
    public static int outlineWidth() { return outlineWidth; }

    private static String normalizeHex(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (!hex.matches("(?i)[0-9a-f]{6}")) {
            throw new IllegalArgumentException("Farbe muss im Format RRGGBB angegeben werden.");
        }
        return hex.toUpperCase(Locale.ROOT);
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("main", mainHex);
        properties.setProperty("outline", outlineEnabled ? outlineHex : "off");
        properties.setProperty("outlineWidth", Integer.toString(outlineWidth));
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                properties.store(writer, "Cobweb Farben — RGB als RRGGBB");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Konfiguration konnte nicht gespeichert werden.", exception);
        }
    }
}

