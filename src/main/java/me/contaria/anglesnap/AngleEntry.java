package me.contaria.anglesnap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.JsonHelper;

public class AngleEntry {
    public String name;
    public float yaw;
    public float pitch;
    public int color;

    public AngleEntry(float yaw, float pitch) {
        this("", yaw, pitch);
    }

    public AngleEntry(String name, float yaw, float pitch) {
        this(name, yaw, pitch, Colors.RED);
    }

    public AngleEntry(String name, float yaw, float pitch, int color) {
        this.name = name;
        this.yaw = yaw;
        this.pitch = pitch;
        this.color = color;
    }

    public void snap() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.setAngles(this.yaw, this.pitch);
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive(this.name));
        jsonObject.add("yaw", new JsonPrimitive(this.yaw));
        jsonObject.add("pitch", new JsonPrimitive(this.pitch));
        jsonObject.add("color", new JsonPrimitive(this.color));
        return jsonObject;
    }

    public static AngleEntry fromJson(JsonObject jsonObject) {
        return new AngleEntry(
                JsonHelper.getString(jsonObject, "name"),
                JsonHelper.getFloat(jsonObject, "yaw"),
                JsonHelper.getFloat(jsonObject, "pitch"),
                JsonHelper.getInt(jsonObject, "color", Colors.RED)
        );
    }
}
