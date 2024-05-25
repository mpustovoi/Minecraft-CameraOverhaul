#if FABRIC_LOADER && !MC_BTA
package mirsario.cameraoverhaul.fabric.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
#if MC_VERSION < "11500"
import org.lwjgl.opengl.*;
#endif

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow public abstract float getXRot();
	@Shadow public abstract float getYRot();
	@Shadow public abstract Vec3 getPosition();
	@Shadow protected abstract void setRotation(float yaw, float pitch);

	@Inject(method = "setup", at = @At("RETURN"))
	private void OnCameraUpdate(BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		var system = CameraOverhaul.instance.system;
		var context = new CameraContext();
		context.velocity = VectorUtils.toJoml(entity.getDeltaMovement());
		context.transform = new Transform(
			VectorUtils.toJoml(getPosition()),
			new Vector3f(getXRot(), getYRot(), 0)
		);
		if (entity instanceof LivingEntity) {
			context.isFlying = ((LivingEntity)entity).isFallFlying();
			context.isSwimming = ((LivingEntity)entity).isSwimming();
		}

#if MC_VERSION < "11500"
		// Undo multiplications.
		GL11.glRotatef((float)context.transform.eulerRot.y + 180.0f, 0f, -1f, 0f);
		GL11.glRotatef((float)context.transform.eulerRot.x, -1f, 0f, 0f);
#endif

		system.OnCameraUpdate(context, tickDelta);
		system.ModifyCameraTransform(context.transform);

		setRotation((float)context.transform.eulerRot.y, (float)context.transform.eulerRot.x);

#if MC_VERSION < "11500"
		// And now redo them.
		GL11.glRotatef((float)context.transform.eulerRot.z, 0f, 0f, 1f);
		GL11.glRotatef((float)context.transform.eulerRot.x, 1f, 0f, 0f);
		GL11.glRotatef((float)context.transform.eulerRot.y + 180f, 0f, 1f, 0f);
#endif
	}
}
#endif