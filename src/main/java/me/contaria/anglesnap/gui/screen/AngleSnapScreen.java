package me.contaria.anglesnap.gui.screen;

import me.contaria.anglesnap.AngleEntry;
import me.contaria.anglesnap.AngleSnap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class AngleSnapScreen extends Screen {
    public AngleSnapScreen() {
        super(Text.translatable("anglesnap.gui.screen.title"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(new TextWidget(0, 10, this.width, 15, this.title, this.textRenderer).alignCenter());
        this.addDrawableChild(new AngleSnapListWidget(this.client, this.width, this.height - 70, 35, this));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    public void snap(AngleEntry angle) {
        angle.snap();
        this.close();
    }

    @Override
    public void removed() {
        AngleSnap.CONFIG.save();
    }
}
