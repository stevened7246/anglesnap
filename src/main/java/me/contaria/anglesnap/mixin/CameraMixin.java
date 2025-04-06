package me.contaria.anglesnap.mixin;

import me.contaria.anglesnap.AngleSnap;
import me.contaria.anglesnap.CameraPosEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @ModifyVariable(
            method = "update",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private boolean modifyThirdPerson(boolean thirdPerson) {
        return thirdPerson || AngleSnap.currentCameraPos != null;
    }

    @ModifyVariable(
            method = "update",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private boolean modifyInverseView(boolean inverseView) {
        return inverseView && AngleSnap.currentCameraPos == null;
    }

    @ModifyVariable(
            method = "setPos(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Vec3d modifyCameraPosition(Vec3d pos) {
        CameraPosEntry entry = AngleSnap.currentCameraPos;
        if (entry != null) {
            return new Vec3d(entry.x, entry.y, entry.z);
        }
        return pos;
    }
}
