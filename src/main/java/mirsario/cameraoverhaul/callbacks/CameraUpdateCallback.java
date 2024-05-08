package mirsario.cameraoverhaul.callbacks;

import mirsario.cameraoverhaul.structures.*;
import net.minecraft.client.*;
import net.minecraft.world.entity.*;

public interface CameraUpdateCallback
{
	Event<CameraUpdateCallback> EVENT = EventHelper.CreateEvent(CameraUpdateCallback.class,
		(listeners) -> (focusedEntity, camera, transform, deltaTime) -> {
			for (CameraUpdateCallback listener : listeners) {
				listener.OnCameraUpdate(focusedEntity, camera, transform, deltaTime);
			}
		}
	);
	
	void OnCameraUpdate(Entity focusedEntity, Camera camera, Transform cameraTransform, float deltaTime);
}