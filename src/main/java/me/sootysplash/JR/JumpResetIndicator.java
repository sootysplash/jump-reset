package me.sootysplash.JR;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpResetIndicator implements ModInitializer {

    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger("jump-reset-indicator");
    public static int hurtAge, jumpAge;
    public static long lastModTime;

    private static ConfigJR config;

    public static ConfigJR getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("JumpResetIndicator | Sootysplash was here");

        ConfigHolder<ConfigJR> holder = AutoConfig.register(ConfigJR.class, GsonConfigSerializer::new);
        holder.registerLoadListener((configHolder, cfg) -> {
            config = cfg;
            return ActionResult.SUCCESS;
        });
        config = holder.get();

        HudRenderCallback.EVENT.register((e, t) -> renderWidget(e));
    }

    private void renderWidget(DrawContext context) {
        ConfigJR configJR = getConfig();

		if(!configJR.enabled)
			return;

		String diff = "No Jump";
		if(lastModTime + 2500 <= System.currentTimeMillis() || Math.abs(jumpAge - hurtAge) >= configJR.ticks) diff = "No Jump";
		else if (jumpAge == hurtAge + 1) diff = "Perfect!";
		else if(hurtAge + 1 < jumpAge) diff = "Late: ".concat(String.valueOf(jumpAge - hurtAge + 1)).concat(" Tick");
		else if(hurtAge + 1 > jumpAge) diff = "Early: ".concat(String.valueOf(hurtAge + 1 - jumpAge)).concat(" Tick");

		int alpha = 50;
		int x = configJR.x;
		int y = configJR.y;
		int xOffset = 80;
		int yOffset = 20;

		if(configJR.background) {

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bf = tess.getBuffer();

			Matrix4f posMat = context.getMatrices().peek().getPositionMatrix();
			bf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

			bf.vertex(posMat, x, y, 0).color(0, 0, 0, alpha).next();
			bf.vertex(posMat, x + xOffset, y, 0).color(0, 0, 0, alpha).next();
			bf.vertex(posMat, x + xOffset, y - yOffset, 0).color(0, 0, 0, alpha).next();
			bf.vertex(posMat, x, y - yOffset, 0).color(0, 0, 0, alpha).next();

			RenderSystem.setShader(GameRenderer::getPositionColorProgram);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

			tess.draw();

		}

		context.drawCenteredTextWithShadow(mc.textRenderer, diff, (int) (x + (xOffset / 2f)), (int) (y - (yOffset / 1.5f)), ColorHelper.Argb.getArgb(0, 255, 255, 255));
	}
}