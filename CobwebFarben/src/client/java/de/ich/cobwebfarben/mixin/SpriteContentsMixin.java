package de.ich.cobwebfarben.mixin;

import de.ich.cobwebfarben.CobwebConfig;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpriteContents.class)
public abstract class SpriteContentsMixin {
    @Shadow @Final private Identifier id;
    @Shadow @Final private NativeImage image;

    @Inject(
        method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/SpriteDimensions;Lnet/minecraft/client/texture/NativeImage;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V",
        at = @At("TAIL")
    )
    private void cobwebfarben$recolorCobweb(CallbackInfo callbackInfo) {
        if (!id.getNamespace().equals("minecraft") || !id.getPath().equals("block/cobweb")) {
            return;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[] original = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                original[y * width + x] = image.getColorArgb(x, y);
            }
        }

        int mainRgb = CobwebConfig.mainRgb();
        int outlineRgb = CobwebConfig.outlineRgb();
        int radius = CobwebConfig.outlineWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = original[y * width + x];
                int alpha = pixel >>> 24;
                if (alpha != 0) {
                    image.setColorArgb(x, y, (alpha << 24) | mainRgb);
                } else if (CobwebConfig.outlineEnabled() && touchesWeb(original, width, height, x, y, radius)) {
                    image.setColorArgb(x, y, 0xFF000000 | outlineRgb);
                }
            }
        }
    }

    private static boolean touchesWeb(int[] pixels, int width, int height, int x, int y, int radius) {
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height
                    && (pixels[ny * width + nx] >>> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

