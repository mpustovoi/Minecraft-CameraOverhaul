package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;

public final class CameraSystem {
	private double prevForwardVelocityPitchOffset;
	private double prevVerticalVelocityPitchOffset;
	private double prevStrafingRollOffset;
	private double prevCameraYaw;
	//private double prevYawDeltaRollOffset;
	private double yawDeltaRollOffset;
	private double yawDeltaRollTargetOffset;
	private final Transform offsetTransform = new Transform();

	public void onCameraUpdate(CameraContext context, double deltaTime) {
		// Reset the offset transform
		offsetTransform.position = new Vector3d(0, 0, 0);
		offsetTransform.eulerRot = new Vector3d(0, 0, 0);

		ConfigData config = CameraOverhaul.instance.config;

		if (!config.enabled) {
			return;
		}

		double strafingRollFactorToUse = config.strafingRollFactor;
		
		if (context.isFlying) {
			strafingRollFactorToUse = config.strafingRollFactorWhenFlying;
		} else if (context.isSwimming) {
			strafingRollFactorToUse = config.strafingRollFactorWhenSwimming;
		}

		var relativeXZVelocity = VectorUtils.rotate(new Vector2d((double)context.velocity.x, (double)context.velocity.z), 360d - (double)context.transform.eulerRot.y);

		// X
		verticalVelocityPitchOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.verticalVelocityPitchFactor, config.verticalVelocitySmoothingFactor);
		forwardVelocityPitchOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.forwardVelocityPitchFactor, config.horizontalVelocitySmoothingFactor);
		// Z
		yawDeltaRollOffset(context, offsetTransform, relativeXZVelocity, deltaTime, config.yawDeltaRollFactor * 1.25d, config.yawDeltaSmoothingFactor, config.yawDeltaDecayFactor);
		strafingRollOffset(context, offsetTransform, relativeXZVelocity, deltaTime, strafingRollFactorToUse, config.horizontalVelocitySmoothingFactor);

		prevCameraYaw = context.transform.eulerRot.y;
	}

	public void modifyCameraTransform(Transform transform) {
		transform.position.add(offsetTransform.position);
		transform.eulerRot.add(offsetTransform.eulerRot);
	}

	private void verticalVelocityPitchOffset(CameraContext context, Transform outputTransform,  Vector2d relativeXZVelocity, double deltaTime, double intensity, double smoothing) {
		double targetVerticalVelocityPitchOffset = context.velocity.y * 2.75d * (context.velocity.y < 0d ? 2.25d : 2.0d);

		if (context.velocity.y < 0d) {
			targetVerticalVelocityPitchOffset *= 2.25d;
		}

		double currentVerticalVelocityPitchOffset = (double) MathUtils.damp(prevVerticalVelocityPitchOffset, targetVerticalVelocityPitchOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentVerticalVelocityPitchOffset * intensity;
		prevVerticalVelocityPitchOffset = currentVerticalVelocityPitchOffset;
	}

	private void forwardVelocityPitchOffset(CameraContext context, Transform outputTransform, Vector2d relativeXZVelocity, double deltaTime, double intensity, double smoothing) {
		double targetForwardVelocityPitchOffset = relativeXZVelocity.y * 5d;
		double currentForwardVelocityPitchOffset = (double)MathUtils.damp(prevForwardVelocityPitchOffset, targetForwardVelocityPitchOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentForwardVelocityPitchOffset * intensity;
		prevForwardVelocityPitchOffset = currentForwardVelocityPitchOffset;
	}

	private void yawDeltaRollOffset(CameraContext context, Transform outputTransform, Vector2d relativeXZVelocity, double deltaTime, double intensity, double offsetSmoothing, double decaySmoothing) {
		double yawDelta = prevCameraYaw - context.transform.eulerRot.y;

		if (yawDelta > 180) {
			yawDelta = 360 - yawDelta;
		} else if (yawDelta < -180) {
			yawDelta = -360 - yawDelta;
		}

		yawDeltaRollTargetOffset += yawDelta * 0.07d;
		yawDeltaRollOffset = MathUtils.damp(yawDeltaRollOffset, yawDeltaRollTargetOffset, offsetSmoothing, deltaTime);

		outputTransform.eulerRot.z += yawDeltaRollOffset * intensity;
		
		yawDeltaRollTargetOffset = MathUtils.damp(yawDeltaRollTargetOffset, 0d, decaySmoothing, deltaTime);
	}

	private void strafingRollOffset(CameraContext context, Transform outputTransform, Vector2d relativeXZVelocity, double deltaTime, double intensity, double smoothing) {
		double strafingRollOffset = -relativeXZVelocity.x * 15d;
		
		prevStrafingRollOffset = strafingRollOffset = MathUtils.damp(prevStrafingRollOffset, strafingRollOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.z += strafingRollOffset * intensity;
	}
}
