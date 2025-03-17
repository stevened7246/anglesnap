package me.contaria.anglesnap.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
    private final Identifier texture;

    public IconButtonWidget(Text message, PressAction onPress, Identifier texture) {
        super(0, 0, 16, 16, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.texture = texture;
        this.setTooltip(Tooltip.of(message));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(RenderLayer::getGuiTextured, this.texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), 16, 16);
    }
}
