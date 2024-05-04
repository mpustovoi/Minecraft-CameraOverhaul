#if FABRIC_LOADER
package mirsario.cameraoverhaul.fabric.mixins;

import mirsario.cameraoverhaul.callbacks.*;
import mirsario.cameraoverhaul.structures.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
#if !MC_VERSION >= "11500"
import org.lwjgl.opengl.*;
#endif

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow public abstract float getYaw();
	@Shadow public abstract float getPitch();
	@Shadow public abstract Vec3d getPos();
	@Shadow protected abstract void setRotation(float yaw, float pitch);

	@Inject(method = "update", at = @At("RETURN"))
	private void OnCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
#if MC_VERSION >= "11500"
		Transform cameraTransform = new Transform(getPos(), new Vec3d(getPitch(), getYaw(), 0d));

		CameraUpdateCallback.EVENT.Invoker().OnCameraUpdate(focusedEntity, (Camera)(Object)this, cameraTransform, tickDelta);

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform((Camera)(Object)this, cameraTransform);

		setRotation((float)cameraTransform.eulerRot.y, (float)cameraTransform.eulerRot.x);
#else
		Transform cameraTransform = new Transform(getPos(), new Vec3d(getPitch(), getYaw(), 0d));

		// Undo multiplications.
		GL11.glRotatef((float)cameraTransform.eulerRot.y + 180.0f, 0f, -1f, 0f);
		GL11.glRotatef((float)cameraTransform.eulerRot.x, -1f, 0f, 0f);

		CameraUpdateCallback.EVENT.Invoker().OnCameraUpdate(focusedEntity, (Camera)(Object)this, cameraTransform, tickDelta);

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform((Camera)(Object)this, cameraTransform);

		setRotation((float)cameraTransform.eulerRot.y, (float)cameraTransform.eulerRot.x);

		// And now redo them.
		GL11.glRotatef((float)cameraTransform.eulerRot.z, 0f, 0f, 1f);
		GL11.glRotatef((float)cameraTransform.eulerRot.x, 1f, 0f, 0f);
		GL11.glRotatef((float)cameraTransform.eulerRot.y + 180f, 0f, 1f, 0f);
#endif
	}
}
#endif