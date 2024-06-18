package mirsario.cameraoverhaul.fabric.mixins.modern;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;
import mirsario.cameraoverhaul.fabric.abstractions.*;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow
	@Final
	private Camera camera;

	@Inject(method = {"tiltViewWhenHurt", "bobViewWhenHurt"}, at = @At("HEAD"))
	private void PostCameraUpdate(MatrixStack matrices, float f, CallbackInfo ci)
	{
		Transform cameraTransform = new Transform(camera.getPos(), new Vec3d(camera.getPitch(), camera.getYaw(), 0d));

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform(camera, cameraTransform);

		MathAbstractions.RotateMatrixByAxis(matrices, 0f, 0f, 1f, (float)cameraTransform.eulerRot.z);
	}
}
