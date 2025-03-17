package me.contaria.anglesnap.gui.screen;

import me.contaria.anglesnap.AngleEntry;
import me.contaria.anglesnap.AngleSnap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class AngleSnapListWidget extends ElementListWidget<AngleSnapListWidget.AbstractEntry> {
    private static final Text NAME_TEXT = Text.translatable("anglesnap.gui.screen.name");
    private static final Text YAW_TEXT = Text.translatable("anglesnap.gui.screen.yaw");
    private static final Text PITCH_TEXT = Text.translatable("anglesnap.gui.screen.pitch");
    private static final Text ADD_TEXT = Text.translatable("anglesnap.gui.screen.add");
    private static final Text DELETE_TEXT = Text.translatable("anglesnap.gui.screen.delete");
    private static final Text EDIT_TEXT = Text.translatable("anglesnap.gui.screen.edit");
    private static final Text SAVE_TEXT = Text.translatable("anglesnap.gui.screen.save");

    private static final Identifier ADD_TEXTURE = Identifier.of("anglesnap", "textures/gui/add.png");
    private static final Identifier DELETE_TEXTURE = Identifier.of("anglesnap", "textures/gui/delete.png");
    private static final Identifier EDIT_TEXTURE = Identifier.of("anglesnap", "textures/gui/edit.png");
    private static final Identifier SAVE_TEXTURE = Identifier.of("anglesnap", "textures/gui/save.png");

    private final AngleSnapScreen parent;

    public AngleSnapListWidget(MinecraftClient minecraftClient, int width, int height, int y, AngleSnapScreen parent) {
        super(minecraftClient, width, height, y, 20, 25);
        this.parent = parent;

        for (AngleEntry angle : AngleSnap.CONFIG.getAngles()) {
            this.addEntry(new Entry(angle));
        }
        this.addEntry(new AddAngleEntry());
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 5;
    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        TextRenderer textRenderer = this.client.textRenderer;
        context.drawText(textRenderer, NAME_TEXT, x + 5, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, YAW_TEXT, x + 5 + 2 * this.width / 5, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
        context.drawText(textRenderer, PITCH_TEXT, x + 5 + 3 * this.width / 5, y + (20 - textRenderer.fontHeight) / 2, Colors.WHITE, true);
    }

    public abstract static class AbstractEntry extends ElementListWidget.Entry<AbstractEntry> {
        protected final MinecraftClient client;

        protected AbstractEntry(MinecraftClient client) {
            this.client = client;
        }

        protected void renderWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, ClickableWidget widget, int x, int y) {
            widget.setX(x);
            widget.setY(y);
            widget.render(context, mouseX, mouseY, tickDelta);
        }

        protected void renderNumberWidgetAt(DrawContext context, int mouseX, int mouseY, float tickDelta, TextFieldWidget widget, int x, int y) {
            if (this.isNumberOrEmpty(widget.getText())) {
                this.renderWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
            } else {
                widget.setEditableColor(Colors.LIGHT_RED);
                widget.setUneditableColor(Colors.LIGHT_RED);
                this.renderWidgetAt(context, mouseX, mouseY, tickDelta, widget, x, y);
                widget.setEditableColor(Colors.WHITE);
                widget.setUneditableColor(Colors.WHITE);
            }
        }

        private boolean isNumberOrEmpty(String text) {
            if (text.isEmpty()) {
                return true;
            }
            try {
                Float.parseFloat(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public class Entry extends AbstractEntry {
        private final AngleEntry angle;

        private final List<ClickableWidget> children;
        private final TextFieldWidget name;
        private final TextFieldWidget yaw;
        private final TextFieldWidget pitch;
        private final ButtonWidget edit;
        private final ButtonWidget save;
        private final ButtonWidget delete;

        private boolean editing;

        public Entry() {
            this(AngleSnap.CONFIG.createAngle());
            this.setEditing(true);
            this.setFocused(this.name);
        }

        public Entry(AngleEntry angle) {
            super(MinecraftClient.getInstance());
            this.angle = angle;
            this.children = new ArrayList<>();

            this.name = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    2 * AngleSnapListWidget.this.width / 5 - 5,
                    20,
                    NAME_TEXT
            ));
            this.name.setText(this.angle.name);
            this.name.setChangedListener(name -> this.angle.name = name);
            this.name.setDrawsBackground(false);
            this.name.setEditableColor(Colors.WHITE);
            this.name.setUneditableColor(Colors.WHITE);

            this.yaw = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    AngleSnapListWidget.this.width / 5 - 5,
                    20,
                    YAW_TEXT
            ));
            this.yaw.setText(String.valueOf(this.angle.yaw));
            this.yaw.setChangedListener(yaw -> {
                try {
                    this.angle.yaw = Float.parseFloat(yaw);
                } catch (NumberFormatException e) {
                    this.angle.yaw = 0.0f;
                }
            });
            this.yaw.setDrawsBackground(false);
            this.yaw.setEditableColor(Colors.WHITE);
            this.yaw.setUneditableColor(Colors.WHITE);

            this.pitch = this.addChild(new TextFieldWidget(
                    this.client.textRenderer,
                    AngleSnapListWidget.this.width / 5 - 5,
                    20,
                    PITCH_TEXT
            ));
            this.pitch.setText(String.valueOf(this.angle.pitch));
            this.pitch.setChangedListener(pitch -> {
                try {
                    this.angle.pitch = Float.parseFloat(pitch);
                } catch (NumberFormatException e) {
                    this.angle.pitch = 0.0f;
                }
            });
            this.pitch.setDrawsBackground(false);
            this.pitch.setEditableColor(Colors.WHITE);
            this.pitch.setUneditableColor(Colors.WHITE);

            this.edit = this.addChild(new IconButtonWidget(EDIT_TEXT, button -> this.toggleEditing(), EDIT_TEXTURE));
            this.save = this.addChild(new IconButtonWidget(SAVE_TEXT, button -> this.toggleEditing(), SAVE_TEXTURE));
            this.delete = this.addChild(new IconButtonWidget(DELETE_TEXT, button -> this.delete(), DELETE_TEXTURE));

            this.setEditing(false);
        }

        private void toggleEditing() {
            this.setEditing(!this.editing);
        }

        private void setEditing(boolean editing) {
            this.editing = editing;

            this.setEditing(this.name, editing);
            this.setEditing(this.yaw, editing);
            this.setEditing(this.pitch, editing);

            this.edit.visible = !editing;
            this.save.visible = editing;
        }

        private void setEditing(TextFieldWidget widget, boolean editing) {
            widget.active = editing;
            widget.setEditable(editing);
            widget.setFocused(false);
            widget.setFocusUnlocked(editing);
        }

        private void delete() {
            AngleSnap.CONFIG.removeAngle(this.angle);
            AngleSnapListWidget.this.removeEntry(this);
        }

        private <T extends ClickableWidget> T addChild(T widget) {
            this.children.add(widget);
            return widget;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.children;
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.setFocused(null);
            }
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = this.client.textRenderer;
            if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, ColorHelper.getArgb(100, 200, 200, 200));
            }
            int textY = y + (entryHeight - textRenderer.fontHeight + 1) / 2;
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.name, x + 5, textY);
            this.renderNumberWidgetAt(context, mouseX, mouseY, tickDelta, this.yaw, x + 5 + 2 * entryWidth / 5, textY);
            this.renderNumberWidgetAt(context, mouseX, mouseY, tickDelta, this.pitch, x + 5 + 3 * entryWidth / 5, textY);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.edit, x + 5 + 4 * entryWidth / 5, y);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.save, x + 5 + 4 * entryWidth / 5, y);
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.delete, x + 5 + 4 * entryWidth / 5 + 20, y);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (!this.editing && button == 0) {
                AngleSnapListWidget.this.parent.snap(this.angle);
                return true;
            }
            return false;
        }
    }

    public class AddAngleEntry extends AbstractEntry {
        private final ButtonWidget add;

        public AddAngleEntry() {
            super(MinecraftClient.getInstance());
            this.add = new IconButtonWidget(ADD_TEXT, button -> this.add(), ADD_TEXTURE);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.add);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.add);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.renderWidgetAt(context, mouseX, mouseY, tickDelta, this.add, x + 5, y + (entryHeight - this.client.textRenderer.fontHeight) / 2);
        }

        private void add() {
            Entry entry = new Entry();
            AngleSnapListWidget.this.removeEntry(this);
            AngleSnapListWidget.this.addEntry(entry);
            AngleSnapListWidget.this.addEntry(this);
            AngleSnapListWidget.this.setFocused(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            return false;
        }
    }
}
