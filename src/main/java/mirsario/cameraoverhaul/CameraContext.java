package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.utilities.*;
import org.joml.*;

public class CameraContext {
	public enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON,
		THIRD_PERSON_REVERSE,
	}

	public boolean isSwimming;
	public boolean isFlying;
	public Vector3d velocity;
	public Perspective perspective;
	public Transform transform = new Transform();

	public Vector3d getForwardRelativeVelocity() {
		var temp = VectorUtils.rotate(new Vector2d(velocity.x, velocity.z), 360d - transform.eulerRot.y);
		return new Vector3d(temp.x, velocity.y, temp.y);
	}
}
