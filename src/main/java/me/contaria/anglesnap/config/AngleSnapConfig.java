package me.contaria.anglesnap.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import me.contaria.anglesnap.AngleEntry;
import me.contaria.anglesnap.AngleSnap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AngleSnapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("anglesnap.json");

    private final List<AngleEntry> angles;

    public AngleSnapConfig() {
        this.angles = new ArrayList<>();
        this.angles.add(new AngleEntry("Example Angle", 3.14f, -31.4f));
        this.load();
        this.save();
    }

    public List<AngleEntry> getAngles() {
        return this.angles;
    }

    public void removeAngle(AngleEntry angle) {
        this.angles.remove(angle);
    }

    public AngleEntry createAngle() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        AngleEntry angle = new AngleEntry(
                "",
                player != null ? (int) (MathHelper.wrapDegrees(player.getYaw()) * 10.0f) / 10.0f : 0.0f,
                player != null ? (int) (MathHelper.wrapDegrees(player.getPitch()) * 10.0f) / 10.0f : 0.0f
        );
        this.angles.add(angle);
        return angle;
    }

    public void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (JsonReader reader = GSON.newJsonReader(Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8))) {
                    this.fromJson(GSON.fromJson(reader, JsonObject.class));
                }
            }
        } catch (Exception e) {
            AngleSnap.LOGGER.error("Failed to read AngleSnap config file!", e);
        }
    }

    private void fromJson(JsonObject jsonObject) {
        this.angles.clear();
        for (JsonElement angle : jsonObject.getAsJsonArray("angles")) {
            this.angles.add(AngleEntry.fromJson(angle.getAsJsonObject()));
        }
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this.toJson()));
        } catch (Exception e) {
            AngleSnap.LOGGER.error("Failed to write AngleSnap config file!", e);
        }
    }

    private JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray angles = new JsonArray();
        for (AngleEntry angle : this.angles) {
            angles.add(angle.toJson());
        }
        jsonObject.add("angles", angles);
        return jsonObject;
    }
}
