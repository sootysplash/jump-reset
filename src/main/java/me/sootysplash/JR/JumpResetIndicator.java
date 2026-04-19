package me.sootysplash.JR;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpResetIndicator implements ModInitializer {
	public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger("jump-reset-indicator");
	public static int hurtAge, jumpAge;
	public static long lastModTime;

	@Override
	public void onInitialize() {
		HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, Identifier.tryParse("jump-reset-indicator", "panel"), this::renderWidget);

		LOGGER.info("JumpResetIndicator | Sootysplash was here");
		AutoConfig.register(ConfigJR.class, GsonConfigSerializer::new);
	}

	private void renderWidget(DrawContext context, RenderTickCounter renderTickCounter) {
		ConfigJR configJR = ConfigJR.getInstance();

		if(!configJR.enabled)
			return;

		String diff = "No Jump";
		if(lastModTime + 2500 <= System.currentTimeMillis() || Math.abs(jumpAge - hurtAge) >= configJR.ticks) diff = "No Jump";
		else if (jumpAge == hurtAge + 1) diff = "Perfect!";
		else if(hurtAge + 1 < jumpAge) diff = "Late: ".concat(String.valueOf(jumpAge - hurtAge + 1)).concat(" Tick");
		else if(hurtAge + 1 > jumpAge) diff = "Early: ".concat(String.valueOf(hurtAge + 1 - jumpAge)).concat(" Tick");

		int x = configJR.x;
		int y = configJR.y;
		int xOffset = 80;
		int yOffset = 20;

		if(configJR.background) {
			context.fill(x, y, x + xOffset, y + yOffset, 0xFF000000);
		}
		context.drawCenteredTextWithShadow(mc.textRenderer, diff, (int) (x + (xOffset / 2f)), y + 5, ColorHelper.getArgb(255, 255, 255, 255));
	}
}