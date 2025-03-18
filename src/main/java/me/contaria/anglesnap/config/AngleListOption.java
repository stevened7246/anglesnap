package me.contaria.anglesnap.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.contaria.anglesnap.AngleEntry;
import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.ArrayList;
import java.util.List;

public class AngleListOption extends Option<List<AngleEntry>> {
    private final List<AngleEntry> angles;

    protected AngleListOption(String id) {
        super(id);
        this.angles = new ArrayList<>();
        this.angles.add(new AngleEntry("Example Angle", 3.14f, -31.4f));
    }

    @Override
    public List<AngleEntry> getValue() {
        return this.angles;
    }

    @Override
    public void setValue(List<AngleEntry> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasWidget() {
        return false;
    }

    @Override
    public ClickableWidget createWidget(int x, int y, int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void fromJson(JsonElement jsonElement) {
        this.angles.clear();
        for (JsonElement angle : jsonElement.getAsJsonArray()) {
            this.angles.add(AngleEntry.fromJson(angle.getAsJsonObject()));
        }
    }

    @Override
    protected JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        for (AngleEntry angle : this.angles) {
            jsonArray.add(angle.toJson());
        }
        return jsonArray;
    }
}
