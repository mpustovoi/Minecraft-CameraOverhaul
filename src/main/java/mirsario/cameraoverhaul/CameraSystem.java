package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;

public final class CameraSystem {
	private float prevForwardVelocityPitchOffset;
	private float prevVerticalVelocityPitchOffset;
	private float prevStrafingRollOffset;
	private float prevCameraYaw;
	//private float prevYawDeltaRollOffset;
	private float yawDeltaRollOffset;
	private float yawDeltaRollTargetOffset;
	private final Transform offsetTransform = new Transform();

	public void onCameraUpdate(CameraContext context, float deltaTime) {
		// Reset the offset transform
		offsetTransform.position = new Vector3f(0, 0, 0);
		offsetTransform.eulerRot = new Vector3f(0, 0, 0);

		ConfigData config = CameraOverhaul.instance.config;

		if (!config.enabled) {
			return;
		}

		float strafingRollFactorToUse = config.strafingRollFactor;
		
		if (context.isFlying) {
			strafingRollFactorToUse = config.strafingRollFactorWhenFlying;
		} else if (context.isSwimming) {
			strafingRollFactorToUse = config.strafingRollFactorWhenSwimming;
		}

		var relativeXZVelocity = VectorUtils.rotate(new Vector2f((float)context.velocity.x, (float)context.velocity.z), 360f - (float)context.transform.eulerRot.y);

		// X
		verticalVelocityPitchOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.verticalVelocityPitchFactor, config.verticalVelocitySmoothingFactor);
		forwardVelocityPitchOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.forwardVelocityPitchFactor, config.horizontalVelocitySmoothingFactor);
		// Z
		yawDeltaRollOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.yawDeltaRollFactor * 1.25f, config.yawDeltaSmoothingFactor, config.yawDeltaDecayFactor);
		strafingRollOffset(context, offsetTransform, relativeXZVelocity, deltaTime, strafingRollFactorToUse, config.horizontalVelocitySmoothingFactor);

		prevCameraYaw = context.transform.eulerRot.y;
	}

	public void modifyCameraTransform(Transform transform) {
		transform.position.add(offsetTransform.position);
		transform.eulerRot.add(offsetTransform.eulerRot);
	}

	private void verticalVelocityPitchOffset(CameraContext context, Transform outputTransform,  Vector2f relativeXZVelocity, float deltaTime, float intensity, float smoothing) {
		float targetVerticalVelocityPitchOffset = context.velocity.y * 2.75f * (context.velocity.y < 0f ? 2.25f : 2.0f);

		if (context.velocity.y < 0f) {
			targetVerticalVelocityPitchOffset *= 2.25f;
		}

		float currentVerticalVelocityPitchOffset = (float) MathUtils.damp(prevVerticalVelocityPitchOffset, targetVerticalVelocityPitchOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentVerticalVelocityPitchOffset * intensity;
		prevVerticalVelocityPitchOffset = currentVerticalVelocityPitchOffset;
	}

	private void forwardVelocityPitchOffset(CameraContext context, Transform outputTransform, Vector2f relativeXZVelocity, float deltaTime, float intensity, float smoothing) {
		float targetForwardVelocityPitchOffset = relativeXZVelocity.y * 5f;
		float currentForwardVelocityPitchOffset = (float)MathUtils.damp(prevForwardVelocityPitchOffset, targetForwardVelocityPitchOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentForwardVelocityPitchOffset * intensity;
		prevForwardVelocityPitchOffset = currentForwardVelocityPitchOffset;
	}

	private void yawDeltaRollOffset(CameraContext context, Transform outputTransform, Vector2f relativeXZVelocity, float deltaTime, float intensity, float offsetSmoothing, float decaySmoothing) {
		float yawDelta = prevCameraYaw - context.transform.eulerRot.y;

		if (yawDelta > 180) {
			yawDelta = 360 - yawDelta;
		} else if (yawDelta < -180) {
			yawDelta = -360 - yawDelta;
		}

		yawDeltaRollTargetOffset += yawDelta * 0.07f;
		yawDeltaRollOffset = MathUtils.damp(yawDeltaRollOffset, yawDeltaRollTargetOffset, offsetSmoothing, deltaTime);

		outputTransform.eulerRot.z += yawDeltaRollOffset * intensity;
		
		yawDeltaRollTargetOffset = MathUtils.damp(yawDeltaRollTargetOffset, 0f, decaySmoothing, deltaTime);
	}

	private void strafingRollOffset(CameraContext context, Transform outputTransform, Vector2f relativeXZVelocity, float deltaTime, float intensity, float smoothing) {
		float strafingRollOffset = -relativeXZVelocity.x * 15f;
		
		prevStrafingRollOffset = strafingRollOffset = MathUtils.damp(prevStrafingRollOffset, strafingRollOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.z += strafingRollOffset * intensity;
	}
}
