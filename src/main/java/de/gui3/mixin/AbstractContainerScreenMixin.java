package de.gui3.mixin;

import de.gui3.Gui3Scale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void gui3$pushContainerScale(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        if (!Gui3Scale.shouldScale(this)) return;

        Screen screen = (Screen) (Object) this;
        double scale = Gui3Scale.factor(this);

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(
                (float) (screen.width * 0.5 + Gui3Scale.offsetX(this)),
                (float) (screen.height * 0.5 + Gui3Scale.offsetY(this))
        );
        pose.scale((float) scale, (float) scale);
        pose.translate(-screen.width * 0.5f, -screen.height * 0.5f);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void gui3$popContainerScale(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        if (Gui3Scale.shouldScale(this)) {
            graphics.pose().popMatrix();
        }
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int gui3$mapRenderMouseX(int mouseX) {
        return (int) Math.round(Gui3Scale.mapX(this, mouseX));
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private int gui3$mapRenderMouseY(int mouseY) {
        return (int) Math.round(Gui3Scale.mapY(this, mouseY));
    }

    @ModifyVariable(method = "mouseClicked", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MouseButtonEvent gui3$mapClick(MouseButtonEvent event) {
        return Gui3Scale.mapEvent(this, event);
    }

    @ModifyVariable(method = "mouseReleased", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MouseButtonEvent gui3$mapRelease(MouseButtonEvent event) {
        return Gui3Scale.mapEvent(this, event);
    }

    @ModifyVariable(method = "mouseDragged", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MouseButtonEvent gui3$mapDragEvent(MouseButtonEvent event) {
        return Gui3Scale.mapEvent(this, event);
    }

    @ModifyVariable(method = "mouseDragged", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double gui3$mapDragX(double deltaX) {
        return Gui3Scale.mapDelta(this, deltaX);
    }

    @ModifyVariable(method = "mouseDragged", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double gui3$mapDragY(double deltaY) {
        return Gui3Scale.mapDelta(this, deltaY);
    }

    @ModifyVariable(method = "mouseScrolled", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double gui3$mapScrollMouseX(double mouseX) {
        return Gui3Scale.mapX(this, mouseX);
    }

    @ModifyVariable(method = "mouseScrolled", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double gui3$mapScrollMouseY(double mouseY) {
        return Gui3Scale.mapY(this, mouseY);
    }
}
