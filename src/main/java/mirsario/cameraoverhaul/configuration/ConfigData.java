package mirsario.cameraoverhaul.configuration;

public class ConfigData {
	public static final int CONFIG_VERSION = 2;

	public int configVersion;
	// Toggles
	public boolean enabled = true;
	public boolean enableInThirdPerson = true;
	// Strafing Roll
	public float strafingRollFactor = 1.0f;
	public float strafingRollFactorWhenFlying = -1.0f;
	public float strafingRollFactorWhenSwimming = -1.0f;
	// Turning Roll
	public float turningRollAccumulation = 1.0f;
	public float turningRollIntensity = 1.0f;
	public float turningRollSmoothing = 1.0f;
	// Pitch factors
	public float verticalVelocityPitchFactor = 1.0f;
	public float forwardVelocityPitchFactor = 1.0f;
	// Smoothing factors
	public float horizontalVelocitySmoothingFactor = 1.0f;
	public float verticalVelocitySmoothingFactor = 1.0f;
	// Sway
	public float cameraSwayIntensity = 1.0f;
	public float cameraSwayFrequency = 1.0f;
	public float cameraSwayFadeInDelay = 1.0f;
	public float cameraSwayFadeInLength = 1.0f;
	public float cameraSwayFadeOutLength = 1.0f;
}