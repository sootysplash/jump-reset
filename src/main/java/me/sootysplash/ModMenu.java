package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.text.Text;

import static me.sootysplash.JumpResetIndicator.mc;


public class ModMenu implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Config config = Config.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(config::save);

            ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
            ConfigEntryBuilder cfgent =  builder.entryBuilder();



            general.addEntry(cfgent.startBooleanToggle(Text.of("Enabled"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Render the Indicator?"))
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            general.addEntry(cfgent.startBooleanToggle(Text.of("BackGround"), config.background)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Render the Background?"))
                    .setSaveConsumer(newValue -> config.background = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("X Position"), config.x, 0, mc.getWindow().getScaledWidth())
                    .setDefaultValue(300)
                    .setTooltip(Text.of("The X Position of the HUD element"))
                    .setSaveConsumer(newValue -> config.x = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("Y Position"), config.y, 0, mc.getWindow().getScaledHeight())
                    .setDefaultValue(200)
                    .setTooltip(Text.of("The Y Position of the HUD element"))
                    .setSaveConsumer(newValue -> config.y = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("Maximum Early/Late Ticks"), config.ticks, 1, 100)
                    .setDefaultValue(10)
                    .setTooltip(Text.of("The Maximum amount of ticks to wait for an Early/Late jump"))
                    .setSaveConsumer(newValue -> config.ticks = newValue)
                    .build());



            return builder.build();
        };
    }

}
