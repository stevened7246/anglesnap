package me.contaria.anglesnap.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.anglesnap.AngleEntry;
import me.contaria.anglesnap.AngleSnap;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    private double lastTickTime;

    @Unique
    private double lastCursorMoveTime;
    @Unique
    private double lastSnapAngleTime;

    @WrapOperation(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            )
    )
    private void snapToAngle(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY, Operation<Void> original) {
        if (this.lastSnapAngleTime + AngleSnap.CONFIG.snapLock.getValue() > this.lastTickTime) {
            cursorDeltaX = 0.0;
            cursorDeltaY = 0.0;
        }

        original.call(player, cursorDeltaX, cursorDeltaY);

        if (cursorDeltaX != 0.0 || cursorDeltaY != 0.0) {
            this.lastCursorMoveTime = this.lastTickTime;
        }
        this.snapToAngle(player);
    }

    @Unique
    private void snapToAngle(ClientPlayerEntity player) {
        if (!(AngleSnap.shouldRenderOverlay() && AngleSnap.CONFIG.snapToAngle.getValue())) {
            return;
        }
        // don't snap to angle if mouse was in motion within snapDelay
        if (this.lastCursorMoveTime + AngleSnap.CONFIG.snapDelay.getValue() >= this.lastTickTime) {
            return;
        }

        AngleEntry closestAngle = null;
        float closestDistance = AngleSnap.CONFIG.snapDistance.getValue();
        for (AngleEntry angle : AngleSnap.CONFIG.getAngles()) {
            float distance = angle.getDistance(MathHelper.wrapDegrees(player.getYaw()), MathHelper.wrapDegrees(player.getPitch()));
            if (distance < closestDistance) {
                closestAngle = angle;
                closestDistance = distance;
            }
        }
        if (closestAngle != null && closestDistance > 0.0f) {
            this.lastSnapAngleTime = this.lastTickTime;
            closestAngle.snap();
        }
    }
}
