package me.sootysplash.JR;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Optional;

import static me.sootysplash.JR.JumpResetIndicator.applyTransformsForTask;
import static me.sootysplash.JR.JumpResetIndicator.mc;

public class ModMenuJR implements ModMenuApi {

    private static double[] getScaleLimits() {
        return new double[]{0.2, 5};
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigJR configJR = ConfigJR.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.nullToEmpty("Config"))
                    .setDoesConfirmSave(false)
                    .setSavingRunnable(configJR::save);

            ConfigCategory general = builder.getOrCreateCategory(Component.nullToEmpty("General"));
            ConfigEntryBuilder cfgent =  builder.entryBuilder();



            general.addEntry(cfgent.startBooleanToggle(Component.nullToEmpty("Enabled"), configJR.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Component.nullToEmpty("Render the Indicator?"))
                    .setSaveConsumer(newValue -> configJR.enabled = newValue)
                    .build());

            general.addEntry(cfgent.startBooleanToggle(Component.nullToEmpty("Background"), configJR.background)
                    .setDefaultValue(false)
                    .setTooltip(Component.nullToEmpty("Render the Background?"))
                    .setSaveConsumer(newValue -> configJR.background = newValue)
                    .build());

            general.addEntry(cfgent.startIntSlider(Component.nullToEmpty("Maximum Early/Late Ticks"), configJR.ticks, 1, 100)
                    .setDefaultValue(10)
                    .setTooltip(Component.nullToEmpty("The Maximum amount of ticks to wait for an Early/Late jump"))
                    .setSaveConsumer(newValue -> configJR.ticks = newValue)
                    .build());

            general.addEntry(cfgent.startAlphaColorField(Component.nullToEmpty("Text Color"), configJR.textColor)
                    .setDefaultValue(new Color(255, 255, 255).getRGB())
                    .setTooltip(Component.nullToEmpty("The color for the element's text"))
                    .setSaveConsumer(newValue -> configJR.textColor = newValue)
                    .build());

            general.addEntry(cfgent.startAlphaColorField(Component.nullToEmpty("Background Color"), configJR.backgroundColor)
                    .setDefaultValue(new Color(0, 0, 0, 50).getRGB())
                    .setTooltip(Component.nullToEmpty("The color for the element's background"))
                    .setSaveConsumer(newValue -> configJR.backgroundColor = newValue)
                    .build());

            general.addEntry(new BooleanListEntry(Component.nullToEmpty("Edit HUD"), false, cfgent.getResetButtonKey(), null, null, () ->
                    Optional.of(mc.level == null
                            ? new Component[]{Component.nullToEmpty("You can only edit the HUD in a world")}
                            : new Component[]{Component.nullToEmpty("Press Escape to cancel"), Component.nullToEmpty("Press Enter to save")}
                    )) {
                @Override
                public Component getYesNoText(boolean bool) {
                    if (bool) {
                        ((Button) this.children().get(0)).onPress(null); // click buttonWidget for true -> false
                        if (mc.level != null) {
                            openHudEditor();
                        }
                    }
                    return Component.literal("Open Editor");
                }
            });

            general.addEntry(cfgent.startDoubleField(Component.nullToEmpty("Scale"), configJR.scale)
                    .setMin(getScaleLimits()[0])
                    .setMax(getScaleLimits()[1])
                    .setDefaultValue(1)
                    .setTooltip(Component.nullToEmpty("The multiplier for the size of the widget"))
                    .setSaveConsumer(newValue -> configJR.scale = newValue)
                    .build());

            general.addEntry(cfgent.startIntField(Component.nullToEmpty("X Position"), configJR.x)
                    .setMin(0)
                    .setDefaultValue(300)
                    .setTooltip(Component.nullToEmpty("The X Position of the HUD element"))
                    .setSaveConsumer(newValue -> configJR.x = newValue)
                    .build());

            general.addEntry(cfgent.startIntField(Component.nullToEmpty("Y Position"), configJR.y)
                    .setMin(0)
                    .setDefaultValue(200)
                    .setTooltip(Component.nullToEmpty("The Y Position of the HUD element"))
                    .setSaveConsumer(newValue -> configJR.y = newValue)
                    .build());



            return builder.build();
        };
    }

    public static String jrScreenName = "jump-reset.hud-editor";

    private static void openHudEditor() {
        ConfigJR config = ConfigJR.getInstance();
        mc.execute(() -> {
            int[] currentOffset = new int[]{config.x, config.y};
            double[] currentScale = new double[]{config.scale};
            mc.setScreen(new Screen(Component.nullToEmpty(jrScreenName)) {
                {
                    addRenderableOnly((graphics, i1, i2, i3) -> {
                        int x = graphics.guiWidth() / 2;
                        int y = graphics.guiHeight() / 2;
                        int col = Color.GRAY.getRGB();
                        graphics.verticalLine(x, 0, y * 2, col);
                        graphics.horizontalLine(0, x * 2, y, col);
                    });

                    DragWidget[] scaleWidget = new DragWidget[1];

                    DragWidget mainWidget = addRenderableWidget(new DragWidget(0, 0, 0, 0) {

                        @Override
                        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                            if (isDraggingMovement) {
                                currentOffset[0] = (int) (startingPos[0] + mouseX - beganDragAt[0]);
                                currentOffset[1] = (int) (startingPos[1] + mouseY - beganDragAt[1]);
                            }

                            double scale = currentScale[0];
                            double inverseScale = 1 / currentScale[0];


                            int white = Color.WHITE.getRGB();
                            int bgX, bgY, bgW, bgH;
                            int[] wh = JumpResetIndicator.getWidthHeight();

                            this.setX(bgX = currentOffset[0]);
                            this.setY(bgY = currentOffset[1]);
                            this.setWidth(bgW = (int) (wh[0] * scale));
                            this.setHeight(bgH = (int) (wh[1] * scale));

                            graphics.fill(bgX, bgY, bgX + bgW, bgY + bgH, new Color(255, 255, 255, 90).getRGB());
                            // outline
                            graphics.fill(bgX, bgY, bgX + bgW, bgY + 1, white);
                            graphics.fill(bgX, bgY + bgH - 1, bgX + bgW, bgY + bgH, white);
                            graphics.fill(bgX, bgY + 1, bgX + 1, bgY + bgH - 1, white);
                            graphics.fill(bgX + bgW - 1, bgY + 1, bgX + bgW, bgY + bgH - 1, white);

                            applyTransformsForTask(graphics, (float) currentScale[0], () -> JumpResetIndicator.renderWidget(graphics,
                                    (int) (currentOffset[0] * inverseScale),
                                    (int) (currentOffset[1] * inverseScale)));
                        }

                        @Override
                        public void onClick(MouseButtonEvent click, boolean doubleClick) {
                            double mx = click.x();
                            double my = click.y();
                            DragWidget sw = scaleWidget[0];
                            if (sw.getX() < mx && sw.getY() < my &&
                                    sw.getX() + sw.getWidth() > mx && sw.getY() + sw.getHeight() > my) {
                                sw.onClick(click, doubleClick);
                                return;
                            }
                            super.onClick(click, doubleClick);
                            startingPos[0] = currentOffset[0];
                            startingPos[1] = currentOffset[1];
                        }

                        @Override
                        public void onRelease(MouseButtonEvent click) {
                            scaleWidget[0].onRelease(click);
                            super.onRelease(click);
                        }

                        private final double[] startingPos = new double[2];

                    });

                    scaleWidget[0] = addRenderableWidget(new DragWidget(0, 0, 0, 0) {

                        private final double[] startingScale = new double[1];

                        @Override
                        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                            if (isDraggingMovement) {
                                double rawNewScale = startingScale[0] + (mouseX - beganDragAt[0] + mouseY - beganDragAt[1]) / 100.0;
                                currentScale[0] = Math.max(getScaleLimits()[0], Math.min(getScaleLimits()[1], rawNewScale));
                            }
                            int x = mainWidget.getX();
                            int y = mainWidget.getY();
                            int w = mainWidget.getWidth();
                            int h = mainWidget.getHeight();

                            int longerSide = (int) Math.max(Math.max(Math.abs(w) * 0.1, Math.abs(h) * 0.1), 5); // 0.1 -> 10% of the corner
                            int padding = 1;
                            int rX = x + w - longerSide - padding;
                            int rY = y + h - longerSide - padding;
                            this.setX(rX);
                            this.setY(rY);
                            this.setWidth(longerSide);
                            this.setHeight(longerSide);

                            graphics.fill(rX, rY, rX + longerSide, rY + longerSide, Color.CYAN.getRGB());
                        }

                        @Override
                        public void onClick(MouseButtonEvent click, boolean doubleClick) {
                            super.onClick(click, doubleClick);
                            startingScale[0] = currentScale[0];
                        }

                    });
                }

                @Override
                public boolean keyPressed(KeyEvent keyEvent) {
                    int key = keyEvent.key();
                    if (key == GLFW.GLFW_KEY_ESCAPE) {
                        this.onClose();
                        return true;
                    }
                    if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                        this.onClose();
                        config.x = currentOffset[0];
                        config.y = currentOffset[1];
                        config.scale = currentScale[0];
                        return true;
                    }
                    return super.keyPressed(keyEvent);
                }

                @Override
                public void onClose() {
                    mc.setScreen(new ModMenuJR().getModConfigScreenFactory().create(null));
                }

                @Override
                public boolean isPauseScreen() {
                    return false;
                }
            });
        });
    }

    private abstract static class DragWidget extends AbstractWidget {
        public DragWidget(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());
        }
        @Override protected void updateWidgetNarration(NarrationElementOutput output) {}

        protected boolean isDraggingMovement = false;
        protected double[] beganDragAt = new double[2];

        @Override
        protected abstract void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mX, int mY, float a);

        @Override
        public void onClick(MouseButtonEvent click, boolean doubleClick) {
            isDraggingMovement = true;
            beganDragAt[0] = click.x();
            beganDragAt[1] = click.y();
        }

        @Override
        public void onRelease(MouseButtonEvent click) {
            isDraggingMovement = false;
        }

    }

}
