package me.sootysplash.JR.integration;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.sootysplash.JR.ConfigJR;
import me.sootysplash.JR.JumpResetIndicator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 */
public class ClothConfigIntegration {

    public static Screen createConfigScreen(Screen parent) {
        ConfigJR config = JumpResetIndicator.getConfig();

        ConfigBuilder builder = ConfigBuilder
                .create()
                .setParentScreen(parent)
                .setTitle(Text.of("General"))
                .transparentBackground();

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enabled"), config.enabled)
                .setTooltip(Text.of("Render the Indicator?"))
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Text.of("Ticks"), config.ticks)
                .setTooltip(Text.of("The maximum amount of ticks to wait for an early/late jump"))
                .setSaveConsumer(newValue -> config.ticks = newValue)
                .build());

        BooleanListEntry background = entryBuilder.startBooleanToggle(Text.of("Background"), config.background)
                .setTooltip(Text.of("Render the Background?"))
                .setSaveConsumer(newValue -> config.background = newValue)
                .build();
        general.addEntry(background);

        Window window = MinecraftClient.getInstance().getWindow();
        general.addEntry(entryBuilder.startIntSlider(Text.of("X Position"), config.x, 0, window.getWidth())
                .setTooltip(Text.of("The X position of the HUD element"))
                .setSaveConsumer(newValue -> config.x = newValue).build());
        general.addEntry(entryBuilder.startIntSlider(Text.of("Y Position"), config.y, 0, window.getHeight())
                .setTooltip(Text.of("The Y position of the HUD element"))
                .setSaveConsumer(newValue -> config.y = newValue).build());
        general.addEntry(entryBuilder.startIntSlider(Text.of("Alpha"), config.alpha, 0, 255)
                .setTooltip(Text.of("The alpha of the HUD element background"))
                .setDisplayRequirement(Requirement.isTrue(background))
                .setSaveConsumer(newValue -> config.alpha = newValue).build());

        return builder.build();
    }
}