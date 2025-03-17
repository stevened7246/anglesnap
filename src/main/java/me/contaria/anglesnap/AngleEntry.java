package me.contaria.anglesnap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AngleEntry {
    public String name;
    public float yaw;
    public float pitch;

    public AngleEntry(String name, float yaw, float pitch) {
        this.name = name;
        this.yaw = yaw;
        this.pitch = pitch;
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
        return jsonObject;
    }

    public static AngleEntry fromJson(JsonObject jsonObject) {
        return new AngleEntry(
                jsonObject.get("name").getAsString(),
                jsonObject.get("yaw").getAsFloat(),
                jsonObject.get("pitch").getAsFloat()
        );
    }
}
