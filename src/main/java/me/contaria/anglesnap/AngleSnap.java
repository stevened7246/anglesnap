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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Objects;

public class AngleSnap implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AngleSnapConfig CONFIG = new AngleSnapConfig();

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
                renderMarker(context, angle, AngleSnap.CONFIG.markerScale.getValue(), AngleSnap.CONFIG.textScale.getValue(), Colors.RED);
            }
        });
    }

    private static boolean shouldRenderOverlay() {
        return openOverlay.isPressed();
    }

    private static void renderMarker(WorldRenderContext context, AngleEntry angle, float markerScale, float textScale, int color) {
        markerScale = markerScale / 2.0f;
        textScale = textScale / 10.0f;

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = context.camera();
        Vector3f pos = Vec3d.fromPolar(
                MathHelper.wrapDegrees(angle.pitch),
                MathHelper.wrapDegrees(angle.yaw + 180.0f)
        ).multiply(5.0).toVector3f();

        MatrixStack matrices = Objects.requireNonNull(context.matrixStack());
        matrices.push();
        matrices.translate(-pos.x(), pos.y(), -pos.z());
        matrices.multiply(camera.getRotation());

        matrices.scale(markerScale, -markerScale, markerScale);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer consumer = Objects.requireNonNull(context.consumers()).getBuffer(RenderLayer.getDebugQuads());
        consumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(color);
        consumer.vertex(matrix4f, -1.0f, 1.0f, 0.0f).color(color);
        consumer.vertex(matrix4f, 1.0f, 1.0f, 0.0f).color(color);
        consumer.vertex(matrix4f, 1.0f, -1.0f, 0.0f).color(color);

        matrices.scale(1.0f / markerScale, 1.0f / -markerScale, 1.0f / markerScale);

        matrices.scale(textScale, -textScale, textScale);
        TextRenderer textRenderer = client.textRenderer;
        float x = -textRenderer.getWidth(angle.name) / 2.0f;
        int backgroundColor = (int) (client.options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;
        textRenderer.draw(
                angle.name, x, -15.0f, Colors.WHITE, false, matrix4f, context.consumers(), TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, 15
        );
        matrices.pop();
    }
}
