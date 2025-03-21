package me.contaria.anglesnap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class AngleEntry {
    public String name;
    public float yaw;
    public float pitch;
    public int icon;
    public int color;

    public AngleEntry(float yaw, float pitch) {
        this("", yaw, pitch);
    }

    public AngleEntry(String name, float yaw, float pitch) {
        this(name, yaw, pitch, 0, Colors.RED);
    }

    public AngleEntry(String name, float yaw, float pitch, int icon, int color) {
        this.name = name;
        this.yaw = yaw;
        this.pitch = pitch;
        this.icon = icon;
        this.color = color;
    }

    public Identifier nextIcon() {
        Identifier next = this.getIcon(++this.icon);
        if (MinecraftClient.getInstance().getResourceManager().getResource(next).isPresent()) {
            return next;
        }
        return this.getIcon(this.icon = 0);
    }

    public Identifier getIcon() {
        return this.getIcon(this.icon);
    }

    private Identifier getIcon(int icon) {
        return Identifier.of("anglesnap", "textures/gui/marker-" + icon + ".png");
    }

    public float getDistance(float yaw, float pitch) {
        yaw = Math.abs(MathHelper.wrapDegrees(yaw) - MathHelper.wrapDegrees(this.yaw));
        pitch = Math.abs(MathHelper.wrapDegrees(pitch) - MathHelper.wrapDegrees(this.pitch));
        return (float) Math.sqrt(yaw * yaw + pitch * pitch);
    }

    public void snap() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.setAngles(this.yaw, this.pitch);
        }
    }

    public static JsonObject toJson(AngleEntry angle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive(angle.name));
        jsonObject.add("yaw", new JsonPrimitive(angle.yaw));
        jsonObject.add("pitch", new JsonPrimitive(angle.pitch));
        jsonObject.add("icon", new JsonPrimitive(angle.icon));
        jsonObject.add("color", new JsonPrimitive(angle.color));
        return jsonObject;
    }

    public static AngleEntry fromJson(JsonObject jsonObject) {
        return new AngleEntry(
                JsonHelper.getString(jsonObject, "name"),
                JsonHelper.getFloat(jsonObject, "yaw"),
                JsonHelper.getFloat(jsonObject, "pitch"),
                JsonHelper.getInt(jsonObject, "icon", 0),
                JsonHelper.getInt(jsonObject, "color", Colors.RED)
        );
    }

    public static JsonObject listToJson(List<AngleEntry> angles) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (AngleEntry angle : angles) {
            jsonArray.add(toJson(angle));
        }
        jsonObject.add("angles", jsonArray);
        return jsonObject;
    }

    public static List<AngleEntry> listFromJson(JsonObject jsonObject) {
        List<AngleEntry> angles = new ArrayList<>();
        for (JsonElement angle : jsonObject.getAsJsonArray("angles")) {
            angles.add(fromJson(angle.getAsJsonObject()));
        }
        return angles;
    }
}
