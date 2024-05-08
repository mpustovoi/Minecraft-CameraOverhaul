package mirsario.cameraoverhaul.structures;

import net.minecraft.world.phys.*;

public class Transform
{
	public Vec3 position;
	public Vec3 eulerRot;

	public Transform()
	{
		this.position = new Vec3(0d, 0d, 0d);
		this.eulerRot = new Vec3(0d, 0d, 0d);
	}

	public Transform(Vec3 position, Vec3 eulerRot)
	{
		this.position = position;
		this.eulerRot = eulerRot;
	}
}