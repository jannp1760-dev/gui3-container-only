package de.gui3;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** In-game configuration menu for the container-only scale. */
public final class Gui3SettingsScreen extends Screen {
    private final Screen parent;
    private final Gui3Config config = Gui3Config.get();

    public Gui3SettingsScreen(Screen parent) {
        super(Component.literal("GUI 3 - Container-Einstellungen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(430, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int fullWidth = panelWidth - 20;
        int halfWidth = (fullWidth - 8) / 2;
        int y = Math.max(28, (this.height - 238) / 2) + 8;

        this.addRenderableWidget(new ValueSlider(
                left + 10, y, fullWidth, 20,
                "GUI-Groesse", config.targetScale, 1.0, 6.0, 0.25,
                value -> config.targetScale = value
        ));

        y += 26;
        this.addRenderableWidget(new ValueSlider(
                left + 10, y, halfWidth, 20,
                "Position X", config.offsetX, -100.0, 100.0, 1.0,
                value -> config.offsetX = value
        ));
        this.addRenderableWidget(new ValueSlider(
                left + 18 + halfWidth, y, halfWidth, 20,
                "Position Y", config.offsetY, -100.0, 100.0, 1.0,
                value -> config.offsetY = value
        ));

        y += 30;
        this.addRenderableWidget(toggleButton(
                left + 10, y, halfWidth, 20,
                "Mod aktiv", config.enabled, value -> config.enabled = value
        ));
        this.addRenderableWidget(toggleButton(
                left + 18 + halfWidth, y, halfWidth, 20,
                "Spielerinventar", config.playerInventory, value -> config.playerInventory = value
        ));

        y += 24;
        this.addRenderableWidget(toggleButton(
                left + 10, y, halfWidth, 20,
                "Kisten / Shulker", config.storageContainers, value -> config.storageContainers = value
        ));
        this.addRenderableWidget(toggleButton(
                left + 18 + halfWidth, y, halfWidth, 20,
                "Werkbaenke / Oefen", config.workstations, value -> config.workstations = value
        ));

        y += 24;
        this.addRenderableWidget(toggleButton(
                left + 10, y, fullWidth, 20,
                "Andere Container", config.otherContainers, value -> config.otherContainers = value
        ));

        y += 30;
        this.addRenderableWidget(Button.builder(Component.literal("Zuruecksetzen"), button -> {
            config.reset();
            this.minecraft.setScreen(new Gui3SettingsScreen(parent));
        }).bounds(left + 10, y, halfWidth, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Fertig"), button -> close())
                .bounds(left + 18 + halfWidth, y, halfWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font,
                "Hotbar und HUD bleiben unveraendert",
                this.width / 2, this.height - 18, 0xB8D8E8);
    }

    @Override
    public void onClose() {
        close();
    }

    private void close() {
        config.save();
        this.minecraft.setScreen(parent);
    }

    private interface DoubleConsumer {
        void accept(double value);
    }

    private interface BooleanConsumer {
        void accept(boolean value);
    }

    private static Button toggleButton(int x, int y, int width, int height,
                                       String label, boolean initial,
                                       BooleanConsumer consumer) {
        final boolean[] state = {initial};
        return Button.builder(
                Component.literal(label + ": " + (initial ? "AN" : "AUS")),
                button -> {
                    state[0] = !state[0];
                    consumer.accept(state[0]);
                    button.setMessage(Component.literal(label + ": " + (state[0] ? "AN" : "AUS")));
                }
        ).bounds(x, y, width, height).build();
    }

    private static final class ValueSlider extends AbstractSliderButton {
        private final String label;
        private final double min;
        private final double max;
        private final double step;
        private final DoubleConsumer consumer;

        ValueSlider(int x, int y, int width, int height, String label,
                    double initial, double min, double max, double step,
                    DoubleConsumer consumer) {
            super(x, y, width, height, Component.empty(),
                    (initial - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.consumer = consumer;
            updateMessage();
        }

        private double actualValue() {
            double raw = min + this.value * (max - min);
            return Math.max(min, Math.min(max, Math.round(raw / step) * step));
        }

        @Override
        protected void updateMessage() {
            double actual = actualValue();
            String text = label.equals("GUI-Groesse")
                    ? String.format(java.util.Locale.ROOT, "%.2f", actual).replaceAll("0+$", "").replaceAll("\\.$", "")
                    : String.format(java.util.Locale.ROOT, "%.0f", actual);
            this.setMessage(Component.literal(label + ": " + text));
        }

        @Override
        protected void applyValue() {
            consumer.accept(actualValue());
        }
    }
}
