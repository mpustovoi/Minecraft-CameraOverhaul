package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;
import java.lang.Math;

public final class CameraSystem {
	private static final double BASE_HORIZONTAL_VELOCITY_SMOOTHING = 0.008d;

	private static final double BASE_VERTICAL_PITCH_MULTIPLIER = 2.50d;
	private static final double BASE_VERTICAL_PITCH_SMOOTHING = 0.00004d;
	private static final double BASE_FORWARD_PITCH_MULTIPLIER = 7.00d;
	private static final double BASE_FORWARD_PITCH_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;
	private static final double BASE_TURNING_ROLL_ACCUMULATION = 0.0048d;
	private static final double BASE_TURNING_ROLL_INTENSITY = 1.25d;
	private static final double BASE_TURNING_ROLL_SMOOTHING = 0.0825d;
	private static final double BASE_STRAFING_ROLL_MULTIPLIER = 14.00d;
	private static final double BASE_STRAFING_ROLL_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;

	private ConfigData config;
	private double prevForwardVelocityPitchOffset;
	private double prevVerticalVelocityPitchOffset;
	private double prevStrafingRollOffset;
	private double prevCameraYaw;
	private double turningRollTargetOffset;
	private CameraContext.Perspective prevCameraPerspective;
	private final Transform offsetTransform = new Transform();

	public void onCameraUpdate(CameraContext context, double deltaTime) {
		config = Configuration.get();

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
		prevCameraPerspective = context.perspective;
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
		double decaySmoothing = BASE_TURNING_ROLL_SMOOTHING * config.turningRollSmoothing;
		double intensity = BASE_TURNING_ROLL_INTENSITY * config.turningRollIntensity;
		double accumulation = BASE_TURNING_ROLL_ACCUMULATION * config.turningRollAccumulation;
		double yawDelta = prevCameraYaw - context.transform.eulerRot.y;

		// Don't spazz out when switching perspectives.
		if (context.perspective != prevCameraPerspective) yawDelta = 0.0;

		// Decay
		turningRollTargetOffset = MathUtils.damp(turningRollTargetOffset, 0d, decaySmoothing, deltaTime);
		// Accumulation
		turningRollTargetOffset = MathUtils.clamp(turningRollTargetOffset + (yawDelta * accumulation), -1.0, 1.0);
		// Apply
		var turningRollOffset = MathUtils.clamp01(turningEasing(Math.abs(turningRollTargetOffset))) * intensity * Math.signum(turningRollTargetOffset);
		outputTransform.eulerRot.z += turningRollOffset;
	}
	private static double turningEasing(double x) {
		// https://easings.net/#easeInOutCubic
		return x < 0.5 ? (4 * x * x * x) : (1 - Math.pow(-2 * x + 2, 3) / 2);
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
