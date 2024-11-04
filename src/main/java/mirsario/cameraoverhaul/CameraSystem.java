package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;
import java.lang.Math;

@SuppressWarnings("unused")
public final class CameraSystem {
	private static final double BASE_HORIZONTAL_VELOCITY_SMOOTHING = 0.008d;

	private ConfigData config;
	private final Vector3d prevCameraEulerRot = new Vector3d();
	private final Vector3d prevEntityVelocity = new Vector3d();
	private double lastCameraMovementTime;
	private double lastEntityMovementTime;
	private CameraContext.Perspective prevCameraPerspective;
	private final Transform offsetTransform = new Transform();

	public void onCameraUpdate(CameraContext context, double deltaTime) {
		var time = TimeSystem.getTime();
		config = Configuration.get();

		// Reset the offset transform
		offsetTransform.position = new Vector3d(0, 0, 0);
		offsetTransform.eulerRot = new Vector3d(0, 0, 0);

		if (!config.enabled) {
			return;
		}

		if (!context.velocity.equals(prevEntityVelocity)) lastEntityMovementTime = time;
		if (!context.transform.eulerRot.equals(prevCameraEulerRot)) lastCameraMovementTime = time;

		// X
		verticalVelocityPitchOffset(context, offsetTransform, deltaTime);
		forwardVelocityPitchOffset(context, offsetTransform, deltaTime);
		// Z
		turningRollOffset(context, offsetTransform, deltaTime);
		strafingRollOffset(context, offsetTransform, deltaTime);
		// XY
		noiseOffset(context, offsetTransform, deltaTime);

		prevEntityVelocity.set(context.velocity);
		prevCameraEulerRot.set(context.transform.eulerRot);
		prevCameraPerspective = context.perspective;
	}
	public void modifyCameraTransform(Transform transform) {
		transform.position.add(offsetTransform.position);
		transform.eulerRot.add(offsetTransform.eulerRot);
	}

	private static final double BASE_VERTICAL_PITCH_MULTIPLIER = 2.50d;
	private static final double BASE_VERTICAL_PITCH_SMOOTHING = 0.00004d;
	private double prevVerticalVelocityPitchOffset;
	private void verticalVelocityPitchOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_VERTICAL_PITCH_MULTIPLIER * config.verticalVelocityPitchFactor;
		double smoothing = BASE_VERTICAL_PITCH_SMOOTHING * config.verticalVelocitySmoothingFactor;

		double targetOffset = context.velocity.y * multiplier;
		double currentOffset = MathUtils.damp(prevVerticalVelocityPitchOffset, targetOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentOffset;
		prevVerticalVelocityPitchOffset = currentOffset;
	}

	private static final double BASE_FORWARD_PITCH_MULTIPLIER = 7.00d;
	private static final double BASE_FORWARD_PITCH_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;
	private double prevForwardVelocityPitchOffset;
	private void forwardVelocityPitchOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double multiplier = BASE_FORWARD_PITCH_MULTIPLIER * config.forwardVelocityPitchFactor;
		double smoothing = BASE_FORWARD_PITCH_SMOOTHING * config.horizontalVelocitySmoothingFactor;

		double targetOffset = context.getForwardRelativeVelocity().z * multiplier;
		double currentOffset = MathUtils.damp(prevForwardVelocityPitchOffset, targetOffset, smoothing, deltaTime);
		
		outputTransform.eulerRot.x += currentOffset;
		prevForwardVelocityPitchOffset = currentOffset;
	}

	private static final double BASE_TURNING_ROLL_ACCUMULATION = 0.0048d;
	private static final double BASE_TURNING_ROLL_INTENSITY = 1.25d;
	private static final double BASE_TURNING_ROLL_SMOOTHING = 0.0825d;
	private double turningRollTargetOffset;
	private void turningRollOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double decaySmoothing = BASE_TURNING_ROLL_SMOOTHING * config.turningRollSmoothing;
		double intensity = BASE_TURNING_ROLL_INTENSITY * config.turningRollIntensity;
		double accumulation = BASE_TURNING_ROLL_ACCUMULATION * config.turningRollAccumulation;
		double yawDelta = prevCameraEulerRot.y - context.transform.eulerRot.y;

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

	private static final double BASE_STRAFING_ROLL_MULTIPLIER = 14.00d;
	private static final double BASE_STRAFING_ROLL_SMOOTHING = BASE_HORIZONTAL_VELOCITY_SMOOTHING;
	private double prevStrafingRollOffset;
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

	private static final double BASE_CAMERASWAY_MULTIPLIER = 0.60d;
	private static final double BASE_CAMERASWAY_FREQUENCY = 0.16d;
	private static final double BASE_CAMERASWAY_FADEIN_DELAY = 0.15d;
	private static final double BASE_CAMERASWAY_FADEIN_LENGTH = 5.0d;
	private static final double BASE_CAMERASWAY_FADEOUT_LENGTH = 0.75d;
	private static final double CAMERASWAY_FADING_SMOOTHNESS = 3.0d;
	private double cameraSwayFactor;
	private double cameraSwayFactorTarget;
	private void noiseOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double intensity = BASE_CAMERASWAY_MULTIPLIER * config.cameraSwayIntensity;
		double frequency = BASE_CAMERASWAY_FREQUENCY * config.cameraSwayFrequency;
		double fadeInDelay = BASE_CAMERASWAY_FADEIN_DELAY * config.cameraSwayFadeInDelay;
		double time = TimeSystem.getTime();
		float noiseX = (float)(time * frequency);

		var cameraMovedRecently = (time - lastCameraMovementTime) < fadeInDelay;
		var entityMovedRecently = (time - lastEntityMovementTime) < fadeInDelay;
		var anythingMovedRecently = cameraMovedRecently || entityMovedRecently;
		// Only start a fade-in after the last fade-out has ended.
		if (anythingMovedRecently) {
			cameraSwayFactorTarget = 0d; // Fade-out
		} else if (cameraSwayFactor == cameraSwayFactorTarget) {
			cameraSwayFactorTarget = 1d; // Fade-in
		}

		var cameraSwayFactorFadeLength = (cameraSwayFactorTarget > 0d
			? (BASE_CAMERASWAY_FADEIN_LENGTH * config.cameraSwayFadeInLength)
			: (BASE_CAMERASWAY_FADEOUT_LENGTH * config.cameraSwayFadeOutLength)
		);
		var cameraSwayFactorFadeStep = cameraSwayFactorFadeLength > 0.0 ? deltaTime / cameraSwayFactorFadeLength : 1.0;
		cameraSwayFactor = MathUtils.stepTowards(cameraSwayFactor, cameraSwayFactorTarget, cameraSwayFactorFadeStep);

		var scaledIntensity = intensity * Math.pow(cameraSwayFactor, CAMERASWAY_FADING_SMOOTHNESS);
		var target = new Vector3d(scaledIntensity, scaledIntensity, 0.0);
		var noise = new Vector3d(
			SimplexNoise.noise(noiseX, 420),
			SimplexNoise.noise(noiseX, 1337),
			SimplexNoise.noise(noiseX, 6969)
		);

		outputTransform.eulerRot.add(noise.mul(target));
	}
}
