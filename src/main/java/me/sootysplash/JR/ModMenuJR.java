package me.sootysplash.JR;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.text.Text;

import static me.sootysplash.JR.JumpResetIndicator.mc;


public class ModMenuJR implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigJR configJR = ConfigJR.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(configJR::save);

            ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
            ConfigEntryBuilder cfgent =  builder.entryBuilder();



            general.addEntry(cfgent.startBooleanToggle(Text.of("Enabled"), configJR.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Render the Indicator?"))
                    .setSaveConsumer(newValue -> configJR.enabled = newValue)
                    .build());

            general.addEntry(cfgent.startBooleanToggle(Text.of("BackGround"), configJR.background)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Render the Background?"))
                    .setSaveConsumer(newValue -> configJR.background = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("X Position"), configJR.x, 0, mc.getWindow().getScaledWidth())
                    .setDefaultValue(300)
                    .setTooltip(Text.of("The X Position of the HUD element"))
                    .setSaveConsumer(newValue -> configJR.x = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("Y Position"), configJR.y, 0, mc.getWindow().getScaledHeight())
                    .setDefaultValue(200)
                    .setTooltip(Text.of("The Y Position of the HUD element"))
                    .setSaveConsumer(newValue -> configJR.y = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Text.of("Maximum Early/Late Ticks"), configJR.ticks, 1, 100)
                    .setDefaultValue(10)
                    .setTooltip(Text.of("The Maximum amount of ticks to wait for an Early/Late jump"))
                    .setSaveConsumer(newValue -> configJR.ticks = newValue)
                    .build());



            return builder.build();
        };
    }

}
