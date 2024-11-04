//? if FABRIC_LOADER {
package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
//? if <1.15
/*import org.lwjgl.opengl.*;*/

@Mixin(Camera.class)
@SuppressWarnings("UnusedMixin")
public abstract class CameraMixin {
	@Shadow public abstract float getXRot();
	@Shadow public abstract float getYRot();
	@Shadow public abstract Vec3 getPosition();
	@Shadow protected abstract void setRotation(float yaw, float pitch);

	@Inject(method = "setup", at = @At("RETURN"))
	private void onCameraUpdate(BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		var system = CameraOverhaul.instance.system;
		var context = new CameraContext();
		context.velocity = VectorUtils.toJoml(entity.getDeltaMovement());
		context.transform = new Transform(
			VectorUtils.toJoml(getPosition()),
			new Vector3d(getXRot(), getYRot(), 0)
		);
		context.perspective = (thirdPerson
			? (inverseView ? CameraContext.Perspective.THIRD_PERSON_REVERSE : CameraContext.Perspective.THIRD_PERSON)
			: CameraContext.Perspective.FIRST_PERSON
		);

		if (entity instanceof LivingEntity) {
			context.isFlying = ((LivingEntity)entity).isFallFlying();
			context.isSwimming = entity.isSwimming();
		}

//? if <1.15 {
		/*// Undo multiplications.
		GL11.glRotatef((float)context.transform.eulerRot.y + 180.0f, 0f, -1f, 0f);
		GL11.glRotatef((float)context.transform.eulerRot.x, -1f, 0f, 0f);
*///?}

		TimeSystem.update();
		system.onCameraUpdate(context, TimeSystem.getDeltaTime());
		system.modifyCameraTransform(context.transform);

		setRotation((float)context.transform.eulerRot.y, (float)context.transform.eulerRot.x);

//? if <1.15 {
		/*// And now redo them.
		GL11.glRotatef((float)context.transform.eulerRot.z, 0f, 0f, 1f);
		GL11.glRotatef((float)context.transform.eulerRot.x, 1f, 0f, 0f);
		GL11.glRotatef((float)context.transform.eulerRot.y + 180f, 0f, 1f, 0f);
*///?}
	}
}
//?}