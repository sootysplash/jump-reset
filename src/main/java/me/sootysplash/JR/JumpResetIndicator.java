package me.sootysplash.JR;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
//import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpResetIndicator implements ModInitializer {
	public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Logger LOGGER = LoggerFactory.getLogger("jump-reset-indicator");
	public static int hurtAge, jumpAge;
	public static long lastModTime;

	@Override
	public void onInitialize() {
        // Uncomment this code to replace the deprecated HudRenderCallback method with the new HudElementRegistry system (deprecated as of 1.21.8)
        // Also don't forget to add the necessary imports at the top
        /*
        HudElementRegistry.addLast(
                Identifier.of("jump-reset-indicator", "widget"), (context, tickDelta) -> renderWidget(context)
        );
        */
		HudRenderCallback.EVENT.register((e, t) -> renderWidget(e));
		LOGGER.info("JumpResetIndicator | Sootysplash was here");
		AutoConfig.register(ConfigJR.class, GsonConfigSerializer::new);
	}
	private void renderWidget(DrawContext context){
		ConfigJR configJR = ConfigJR.getInstance();

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

        if (configJR.background) {
            int bgColor = (alpha << 24) | (0);
            context.fill(x, y - yOffset, x + xOffset, y, bgColor);
        }

        int textColor = 0xFFFFFFFF;
        context.drawCenteredTextWithShadow(mc.textRenderer, diff, (int) (x + (xOffset / 2f)), (int) (y - (yOffset / 1.5f)), textColor);
	}
}