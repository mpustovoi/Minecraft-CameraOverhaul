package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import org.apache.logging.log4j.*;

public final class CameraOverhaul {
	public static CameraOverhaul instance;

	public static final String MOD_ID = "cameraoverhaul";
	public static final Logger LOGGER = LogManager.getLogger("CameraOverhaul");

	public CameraSystem system;

	public void onInitializeClient() {
		system = new CameraSystem();
	}
}
