package me.sootysplash.JR;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpResetIndicator implements ModInitializer {
	public static final Minecraft mc = Minecraft.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger("jump-reset-indicator");
	public static int hurtAge, jumpAge;
	public static long lastModTime;

	@Override
	public void onInitialize() {
		HudRenderCallback.EVENT.register((t, e) -> wrappedRenderWidget(t));
		LOGGER.info("JumpResetIndicator | Sootysplash was here");
	}

    public static int[] getWidthHeight() {
        return new int[]{80, 20};
    }

    public static void applyTransformsForTask(GuiGraphics graphics, float scale, Runnable runnable) {
        var stack = graphics.pose();
        stack.pushMatrix();
        stack.scale(scale, scale);
        runnable.run();
        stack.popMatrix();
    }

    private static void wrappedRenderWidget(GuiGraphics context) {
        ConfigJR configJR = ConfigJR.getInstance();
        if (!configJR.enabled) {
            return;
        }

        if (mc.screen != null && mc.screen.getTitle().getString().equals(ModMenuJR.jrScreenName)) {
            return;
        }

        double inverseScale = 1 / configJR.scale;
        applyTransformsForTask(context, (float) configJR.scale, () -> renderWidget(context, (int) (configJR.x * inverseScale), (int) (configJR.y * inverseScale)));
    }

    public static void renderWidget(GuiGraphics context, int x, int y) {
        ConfigJR configJR = ConfigJR.getInstance();
		String diff = "No Jump";
		if(lastModTime + 2500 <= System.currentTimeMillis() || Math.abs(jumpAge - hurtAge) >= configJR.ticks) diff = "No Jump";
		else if (jumpAge == hurtAge + 1) diff = "Perfect!";
		else if(hurtAge + 1 < jumpAge) diff = "Late: ".concat(String.valueOf(jumpAge - hurtAge + 1)).concat(" Tick");
		else if(hurtAge + 1 > jumpAge) diff = "Early: ".concat(String.valueOf(hurtAge + 1 - jumpAge)).concat(" Tick");

		int[] wh = getWidthHeight();

		if (configJR.background) {
            context.fill(x, y, x + wh[0], y + wh[1], configJR.backgroundColor);
		}
        int textW = mc.font.width(diff);
        int drawX = (int) (x + wh[0] / 2f - textW / 2f);
        int drawY = (int) (y + (wh[1] / 3f));
        context.drawString(mc.font, diff, drawX, drawY, configJR.textColor);
	}
}