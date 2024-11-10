package me.sootysplash.JR;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;


@Config(name = "jump-reset-indicator")
public class ConfigJR implements ConfigData {

    public boolean enabled = true;
    public boolean background = false;
    public int ticks = 10;
    public int x = 0;
    public int y = 0;
    public int alpha = 50;
}