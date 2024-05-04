#if FABRIC_LOADER
package mirsario.cameraoverhaul.fabric;

import mirsario.cameraoverhaul.*;
import net.fabricmc.api.*;

public class FabricClientModInitializer implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		if (CameraOverhaul.instance == null) {
			CameraOverhaul.instance = new CameraOverhaul();
		}

		CameraOverhaul.instance.onInitializeClient();
	}
}
#endif