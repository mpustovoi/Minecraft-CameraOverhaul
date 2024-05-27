package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.utilities.Transform;
import org.joml.*;

public class CameraContext {
	public boolean isSwimming;
	public boolean isFlying;
	public Vector3d velocity;
	public Transform transform = new Transform();
}
