package me.contaria.anglesnap;

import com.mojang.logging.LogUtils;
import me.contaria.anglesnap.config.AngleSnapConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Objects;

public class AngleSnap implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AngleSnapConfig CONFIG = new AngleSnapConfig();

    private static final Identifier MARKER_TEXTURE = Identifier.of("anglesnap", "textures/gui/marker.png");

    public static KeyBinding openMenu;
    public static KeyBinding openOverlay;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "anglesnap.key.openmenu",
                GLFW.GLFW_KEY_F6,
                "anglesnap.key"
        ));
        openOverlay = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding(
                "anglesnap.key.openoverlay",
                GLFW.GLFW_KEY_F7,
                "anglesnap.key",
                () -> true
        ));

        WorldRenderEvents.LAST.register(context -> {
            if (!shouldRenderOverlay()) {
                return;
            }
            for (AngleEntry angle : AngleSnap.CONFIG.getAngles()) {
                renderMarker(context, angle, AngleSnap.CONFIG.markerScale.getValue(), AngleSnap.CONFIG.textScale.getValue());
            }
        });
    }

    private static boolean shouldRenderOverlay() {
        return openOverlay.isPressed();
    }

    private static void renderMarker(WorldRenderContext context, AngleEntry angle, float markerScale, float textScale) {
        markerScale = markerScale / 10.0f;
        textScale = textScale / 50.0f;

        Vector3f pos = Vec3d.fromPolar(
                MathHelper.wrapDegrees(angle.pitch),
                MathHelper.wrapDegrees(angle.yaw + 180.0f)
        ).multiply(-1.0, 1.0, -1.0).toVector3f();
        Quaternionf rotation = context.camera().getRotation();

        drawIcon(context, pos, rotation, angle, markerScale);
        if (!angle.name.isEmpty()) {
            drawName(context, pos, rotation, angle, textScale);
        }
    }

    private static void drawIcon(WorldRenderContext context, Vector3f pos, Quaternionf rotation, AngleEntry angle, float scale) {
        if (scale == 0.0f) {
            return;
        }

        MatrixStack matrices = Objects.requireNonNull(context.matrixStack());
        matrices.push();
        matrices.translate(pos.x(), pos.y(), pos.z());
        matrices.multiply(rotation);
        matrices.scale(scale, -scale, scale);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer consumer = Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getGuiTexturedOverlay(MARKER_TEXTURE));
        consumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(angle.color).texture(0.0f, 0.0f);
        consumer.vertex(matrix4f, -1.0f, 1.0f, 0.0f).color(angle.color).texture(0.0f, 1.0f);
        consumer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).color(angle.color).texture(1.0f, 1.0f);
        consumer.vertex(matrix4f, 1.0f, -1.0f, 0.0f).color(angle.color).texture(1.0f, 0.0f);

        matrices.scale(1.0f / scale, 1.0f / -scale, 1.0f / scale);
        matrices.pop();
    }

    private static void drawName(WorldRenderContext context, Vector3f pos, Quaternionf rotation, AngleEntry angle, float scale) {
        if (scale == 0.0f && angle.name.isEmpty()) {
            return;
        }

        MatrixStack matrices = Objects.requireNonNull(context.matrixStack());
        matrices.push();
        matrices.translate(pos.x(), pos.y(), pos.z());
        matrices.multiply(rotation);
        matrices.scale(scale, -scale, scale);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        float x = -textRenderer.getWidth(angle.name) / 2.0f;
        int backgroundColor = (int) (client.options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;
        textRenderer.draw(
                angle.name, x, -15.0f, Colors.WHITE, false, matrix4f, context.consumers(), TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, 15
        );

        matrices.scale(1.0f / scale, 1.0f / -scale, 1.0f / scale);
        matrices.pop();
    }
}
