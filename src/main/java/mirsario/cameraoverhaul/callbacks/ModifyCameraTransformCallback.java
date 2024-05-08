package mirsario.cameraoverhaul.callbacks;

import mirsario.cameraoverhaul.structures.*;
import net.minecraft.client.*;

public interface ModifyCameraTransformCallback
{
	Event<ModifyCameraTransformCallback> EVENT = EventHelper.CreateEvent(ModifyCameraTransformCallback.class,
		(listeners) -> (camera, transform) -> {
			for(ModifyCameraTransformCallback listener : listeners) {
				transform = listener.ModifyCameraTransform(camera, transform);
			}

			return transform;
		}
	);
	
	Transform ModifyCameraTransform(Camera camera, Transform transform);
}