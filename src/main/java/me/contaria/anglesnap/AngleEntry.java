package me.contaria.anglesnap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

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

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive(this.name));
        jsonObject.add("yaw", new JsonPrimitive(this.yaw));
        jsonObject.add("pitch", new JsonPrimitive(this.pitch));
        jsonObject.add("icon", new JsonPrimitive(this.icon));
        jsonObject.add("color", new JsonPrimitive(this.color));
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
}
