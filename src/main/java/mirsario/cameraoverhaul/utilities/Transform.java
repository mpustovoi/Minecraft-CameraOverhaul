package mirsario.cameraoverhaul.utilities;

import org.joml.*;

public class Transform
{
	public Vector3f position;
	public Vector3f eulerRot;

	public Transform()
	{
		this.position = new Vector3f(0, 0, 0);
		this.eulerRot = new Vector3f(0, 0, 0);
	}

	public Transform(Vector3f position, Vector3f eulerRot)
	{
		this.position = position;
		this.eulerRot = eulerRot;
	}
}