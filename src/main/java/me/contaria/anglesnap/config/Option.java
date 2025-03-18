package me.contaria.anglesnap.config;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public abstract class Option<T> {
    private final String id;

    protected Option(String id) {
        this.id = id;
    }

    public final String getId() {
        return this.id;
    }

    public final Text getName() {
        return Text.translatable("anglesnap.gui.config.option." + this.getId());
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract boolean hasWidget();

    public abstract ClickableWidget createWidget(int x, int y, int width, int height);

    protected abstract void fromJson(JsonElement jsonElement);

    protected abstract JsonElement toJson();
}
