package me.contaria.anglesnap.config;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

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

    public final Text getMessage() {
        Language language = Language.getInstance();
        String valueSpecified = "anglesnap.gui.config.option." + this.getId() + ".value." + this.getValue();
        if (language.hasTranslation(valueSpecified)) {
            return Text.translatable(valueSpecified);
        }
        String value = "anglesnap.gui.config.option." + this.getId() + ".value";
        if (language.hasTranslation(value)) {
            return Text.translatable(value, this.getDefaultMessage());
        }
        return this.getDefaultMessage();
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract boolean hasWidget();

    public abstract ClickableWidget createWidget(int x, int y, int width, int height);

    public abstract Text getDefaultMessage();

    protected abstract void fromJson(JsonElement jsonElement);

    protected abstract JsonElement toJson();
}
