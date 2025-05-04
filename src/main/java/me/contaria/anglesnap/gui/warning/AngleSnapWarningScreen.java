package me.contaria.anglesnap.gui.warning;

import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class AngleSnapWarningScreen extends WarningScreen {
    private final Consumer<Boolean> onConfirm;
    private final Runnable onCancel;

    private AngleSnapWarningScreen(Text header, Text message, Text checkbox, Text narration, Consumer<Boolean> onConfirm, Runnable onCancel) {
        super(header, message, checkbox, narration);
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget layout = DirectionalLayoutWidget.horizontal().spacing(8);
        layout.add(ButtonWidget.builder(ScreenTexts.PROCEED, button -> this.onConfirm.accept(this.checkbox != null && this.checkbox.isChecked())).build());
        layout.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.onCancel.run()).build());
        return layout;
    }

    public static AngleSnapWarningScreen create(Consumer<Boolean> onConfirm, Runnable onCancel) {
        Text header = Text.translatable("anglesnap.gui.warning.header");
        Text message = Text.translatable("anglesnap.gui.warning.message");
        Text checkbox = Text.translatable("anglesnap.gui.warning.checkbox");
        return new AngleSnapWarningScreen(
                header,
                message,
                checkbox,
                header.copy().append("\n").append(message),
                onConfirm,
                onCancel
        );
    }
}
