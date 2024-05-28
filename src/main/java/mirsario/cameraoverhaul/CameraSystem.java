package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;

public final class CameraSystem {
	private static final double BASE_HORIZONTAL_VELOCITY_SMOOTHING = 0.00007d;

	private static final double BASE_VERTICAL_PITCH_MULTIPLIER = 2.50d;
	private static final double BASE_VERTICAL_PITCH_SMOOTHING = 0.0000001d;
	private static final double BASE_FORWARD_PITCH_MULTIPLIER = 7.00d;
	private static final double BASE_FORWARD_PITCH_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;
	private static final double BASE_TURNING_ROLL_MULTIPLIER = 0.05d;
	private static final double BASE_TURNING_ROLL_SMOOTHING = 0.000006d;
	private static final double BASE_TURNING_ROLL_DECAY = 0.00006d;
	private static final double BASE_STRAFING_ROLL_MULTIPLIER = 14.00d;
	private static final double BASE_STRAFING_ROLL_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;

	private double prevForwardVelocityPitchOffset;
	private double prevVerticalVelocityPitchOffset;
	private double prevStrafingRollOffset;
	private double prevCameraYaw;
	//private double prevTurningRollOffset;
	private double turningRollOffset;
	private double turningRollTargetOffset;
	private ConfigData config;
	private final Transform offsetTransform = new Transform();

	public void onCameraUpdate(CameraContext context, double deltaTime) {
		config = CameraOverhaul.instance.config;

		// Reset the offset transform
		offsetTransform.position = new Vector3d(0, 0, 0);
		offsetTransform.eulerRot = new Vector3d(0, 0, 0);

		if (!config.enabled) {
			return;
		}

		// X
		verticalVelocityPitchOffset(context, offsetTransform, deltaTime);
		forwardVelocityPitchOffset(context, offsetTransform, deltaTime);
		// Z
		turningRollOffset(context, offsetTransform, deltaTime);
		strafingRollOffset(context, offsetTransform, deltaTime);

		prevCameraYaw = context.transform.eulerRot.y;
	}

	public void modifyCameraTransform(Transform transform) {
		transform.position.add(offsetTransform.position);
		transform.eulerRot.add(offsetTransform.eulerRot);
	}

	private void verticalVelocityPitchOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_VERTICAL_PITCH_MULTIPLIER * config.verticalVelocityPitchFactor;
		double smoothing = BASE_VERTICAL_PITCH_SMOOTHING * config.verticalVelocitySmoothingFactor;

		double targetOffset = context.velocity.y * multiplier;
		double currentOffset = MathUtils.damp(prevVerticalVelocityPitchOffset, targetOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentOffset;
		prevVerticalVelocityPitchOffset = currentOffset;
	}

	private void forwardVelocityPitchOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_FORWARD_PITCH_MULTIPLIER * config.forwardVelocityPitchFactor;
		double smoothing = BASE_FORWARD_PITCH_SMOOTHING * config.horizontalVelocitySmoothingFactor;

		double targetOffset = context.getForwardRelativeVelocity().z * multiplier;
		double currentOffset = MathUtils.damp(prevForwardVelocityPitchOffset, targetOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentOffset;
		prevForwardVelocityPitchOffset = currentOffset;
	}

	private void turningRollOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_TURNING_ROLL_MULTIPLIER * config.yawDeltaRollFactor;
		double offsetSmoothing = BASE_TURNING_ROLL_SMOOTHING * config.yawDeltaSmoothingFactor;
		double decaySmoothing = BASE_TURNING_ROLL_DECAY * config.yawDeltaDecayFactor;

		double yawDelta = prevCameraYaw - context.transform.eulerRot.y;

		if (yawDelta > 180) {
			yawDelta = 360 - yawDelta;
		} else if (yawDelta < -180) {
			yawDelta = -360 - yawDelta;
		}

		turningRollTargetOffset += yawDelta * multiplier;
		turningRollOffset = MathUtils.damp(turningRollOffset, turningRollTargetOffset, offsetSmoothing, deltaTime);

		outputTransform.eulerRot.z += turningRollOffset;
		
		turningRollTargetOffset = MathUtils.damp(turningRollTargetOffset, 0d, decaySmoothing, deltaTime);
	}

	private void strafingRollOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_STRAFING_ROLL_MULTIPLIER;
		double smoothing = BASE_STRAFING_ROLL_SMOOTHING * config.horizontalVelocitySmoothingFactor;

		if (context.isFlying) {
			multiplier *= config.strafingRollFactorWhenFlying;
		} else if (context.isSwimming) {
			multiplier *= config.strafingRollFactorWhenSwimming;
		} else {
			multiplier *= config.strafingRollFactor;
		}

		double target = -context.getForwardRelativeVelocity().x * multiplier;
		double offset = MathUtils.damp(prevStrafingRollOffset, target, smoothing, deltaTime);

		outputTransform.eulerRot.z += offset;
		prevStrafingRollOffset = offset;
	}
}
