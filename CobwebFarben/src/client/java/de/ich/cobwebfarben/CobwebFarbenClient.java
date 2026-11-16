package de.ich.cobwebfarben;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class CobwebFarbenClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobwebConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("webfarben")
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal(
                        "Spinnweben: Hauptfarbe #" + CobwebConfig.mainHex()
                            + ", Außenlinie " + (CobwebConfig.outlineEnabled() ? "#" + CobwebConfig.outlineHex() : "aus")
                            + ", Breite " + CobwebConfig.outlineWidth()
                            + ". Befehle: /webfarben haupt <HEX>, /webfarben linie <HEX|aus>, /webfarben breite <1-3>, /webfarben reset"
                    ));
                    return 1;
                })
                .then(ClientCommandManager.literal("haupt")
                    .then(ClientCommandManager.argument("farbe", StringArgumentType.word())
                        .executes(context -> {
                            try {
                                CobwebConfig.setMainColor(StringArgumentType.getString(context, "farbe"));
                                return changed(context.getSource(), "Hauptfarbe", CobwebConfig.mainHex());
                            } catch (IllegalArgumentException exception) {
                                context.getSource().sendError(Text.literal("Ungültige Farbe. Bitte RRGGBB angeben, z. B. FF55FF."));
                                return 0;
                            }
                        })))
                .then(ClientCommandManager.literal("linie")
                    .then(ClientCommandManager.argument("farbe", StringArgumentType.word())
                        .executes(context -> {
                            String value = StringArgumentType.getString(context, "farbe");
                            if (value.equalsIgnoreCase("aus") || value.equalsIgnoreCase("off")) {
                                CobwebConfig.setOutlineColor(value);
                                return changed(context.getSource(), "Außenlinie", "aus");
                            }
                            try {
                                CobwebConfig.setOutlineColor(value);
                                return changed(context.getSource(), "Außenlinie", CobwebConfig.outlineHex());
                            } catch (IllegalArgumentException exception) {
                                context.getSource().sendError(Text.literal("Ungültige Farbe. Bitte RRGGBB angeben, z. B. 202020."));
                                return 0;
                            }
                        })))
                .then(ClientCommandManager.literal("breite")
                    .then(ClientCommandManager.argument("pixel", StringArgumentType.word())
                        .executes(context -> {
                            String width = StringArgumentType.getString(context, "pixel");
                            int parsed;
                            try {
                                parsed = Integer.parseInt(width);
                            } catch (NumberFormatException exception) {
                                context.getSource().sendError(Text.literal("Breite muss 1, 2 oder 3 sein."));
                                return 0;
                            }
                            if (parsed < 1 || parsed > 3) {
                                context.getSource().sendError(Text.literal("Breite muss 1, 2 oder 3 sein."));
                                return 0;
                            }
                            CobwebConfig.setOutlineWidth(parsed);
                            return changed(context.getSource(), "Linienbreite", Integer.toString(parsed));
                        })))
                .then(ClientCommandManager.literal("reset").executes(context -> {
                    CobwebConfig.reset();
                    return changed(context.getSource(), "Farben", "Standardwerte");
                }))));
    }

    private static int changed(net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source, String what, String value) {
        source.sendFeedback(Text.literal(what + " gesetzt: " + value + ". Texturen werden aktualisiert …"));
        MinecraftClient.getInstance().reloadResources();
        return 1;
    }
}

