package mirsario.cameraoverhaul.configuration;

public class ConfigData {
	public static final int CONFIG_VERSION = 2;

	public boolean enabled = true;
	public int configVersion;
	// Roll
	public float strafingRollFactor = 1.0f;
	public float strafingRollFactorWhenFlying = -1.0f;
	public float strafingRollFactorWhenSwimming = -1.0f;
	public float yawDeltaRollFactor = 1.0f;
	// Pitch factors
	public float verticalVelocityPitchFactor = 1.0f;
	public float forwardVelocityPitchFactor = 1.0f;
	// Smoothing factors
	public float horizontalVelocitySmoothingFactor = 1.0f;
	public float verticalVelocitySmoothingFactor = 1.0f;
	public float yawDeltaSmoothingFactor = 1.0f;
	public float yawDeltaDecayFactor = 1.0f;
}