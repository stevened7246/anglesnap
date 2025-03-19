package me.contaria.anglesnap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AngleSnapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("anglesnap.json");

    private final Map<String, Option<?>> options;

    private final AngleListOption angles;
    public final FloatOption markerScale;
    public final FloatOption textScale;

    public AngleSnapConfig() {
        this.options = new HashMap<>();
        this.angles = this.register(new AngleListOption("angles"));
        this.markerScale = this.register(new FloatOption("markerScale", 0.0f, 1.0f, 0.2f));
        this.textScale = this.register(new FloatOption("textScale", 0.0f, 1.0f, 0.2f));
        this.load();
        this.save();
    }

    private <T extends Option<?>> T register(T option) {
        if (this.options.put(option.getId(), option) != null) {
            throw new IllegalStateException("Tried to register option '" + option.getId() + "' twice!");
        }
        return option;
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
        for (String key : jsonObject.keySet()) {
            if (this.options.containsKey(key)) {
                this.options.get(key).fromJson(jsonObject.get(key));
            }
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
        for (Option<?> option : this.options.values()) {
            jsonObject.add(option.getId(), option.toJson());
        }
        return jsonObject;
    }

    public List<AngleEntry> getAngles() {
        return this.angles.getValue();
    }

    public void removeAngle(AngleEntry angle) {
        this.angles.getValue().remove(angle);
    }

    public AngleEntry createAngle() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        AngleEntry angle = new AngleEntry(
                player != null ? (int) (MathHelper.wrapDegrees(player.getYaw()) * 10.0f) / 10.0f : 0.0f,
                player != null ? (int) (MathHelper.wrapDegrees(player.getPitch()) * 10.0f) / 10.0f : 0.0f
        );
        this.angles.getValue().add(angle);
        return angle;
    }

    public Iterable<Option<?>> getOptions() {
        return this.options.values();
    }
}
