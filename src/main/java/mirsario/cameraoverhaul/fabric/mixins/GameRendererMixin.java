#if FABRIC_LOADER && MC_VERSION >= "11500"
package mirsario.cameraoverhaul.fabric.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.mojang.blaze3d.vertex.*;
import org.joml.*;

@Mixin(GameRenderer.class)
@SuppressWarnings("UnusedMixin")
public abstract class GameRendererMixin {
	@Shadow @Final private Camera mainCamera;

	@Inject(method = "bobHurt", at = @At("HEAD"))
	private void postCameraUpdate(PoseStack matrices, float f, CallbackInfo ci) {
		Transform cameraTransform = new Transform(
			VectorUtils.toJoml(mainCamera.getPosition()),
			new Vector3f(mainCamera.getXRot(), mainCamera.getYRot(), 0)
		);

		CameraOverhaul.instance.system.modifyCameraTransform(cameraTransform);

		//matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)cameraTransform.eulerRot.z));
		MathAbstractions.rotateMatrixByAxis(matrices, 0f, 0f, 1f, (float)cameraTransform.eulerRot.z);
	}
}
#endif