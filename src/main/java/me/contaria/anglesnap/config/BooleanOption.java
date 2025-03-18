package me.contaria.anglesnap.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;

public class BooleanOption extends Option<Boolean> {
    private boolean value;

    protected BooleanOption(String id, boolean defaultValue) {
        super(id);
        this.setValue(defaultValue);
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean hasWidget() {
        return true;
    }

    @Override
    public ClickableWidget createWidget(int x, int y, int width, int height) {
        return ButtonWidget.builder(ScreenTexts.onOrOff(this.getValue()), button -> {
            this.setValue(!this.getValue());
            button.setMessage(ScreenTexts.onOrOff(this.getValue()));
        }).dimensions(x, y, width, height).build();
    }

    @Override
    protected void fromJson(JsonElement jsonElement) {
        this.setValue(jsonElement.getAsBoolean());
    }

    @Override
    protected JsonElement toJson() {
        return new JsonPrimitive(this.getValue());
    }
}
