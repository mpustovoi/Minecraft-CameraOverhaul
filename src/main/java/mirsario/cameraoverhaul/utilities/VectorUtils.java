package mirsario.cameraoverhaul.utilities;

import org.joml.Vector2f;
import org.joml.Vector3f;

public final class VectorUtils
{
	#if MC_RELEASE
	public static Vector2f toJoml(net.minecraft.world.phys.Vec2 vec) {
		return new Vector2f((float)vec.x, (float)vec.y);
	}
	public static Vector3f toJoml(net.minecraft.world.phys.Vec3 vec) {
		return new Vector3f((float)vec.x, (float)vec.y, (float)vec.z);
	}
	#endif

	public static float Length(Vector2f vec)
	{
		return Length(vec.x, vec.y);
	}
	public static float Length(float x, float y)
	{
		return (float)Math.sqrt(x * x + y * y);
	}

	public static Vector2f Rotate(Vector2f vec, float degrees)
	{
		double radians = Math.toRadians(degrees);
		float sin = (float)Math.sin(radians);
		float cos = (float)Math.cos(radians);

		return new Vector2f((cos * vec.x) - (sin * vec.y), (sin * vec.x) + (cos * vec.y));
	}

	public static Vector2f Lerp(Vector2f src, Vector2f dst, float step)
	{
		return Lerp(src.x, src.y, dst.x, dst.y, step);
	}

	public static Vector2f Lerp(float xSrc, float ySrc, float xDst, float yDst, float step)
	{
		return new Vector2f(
			xSrc + (xDst - xSrc) * step,
			ySrc + (yDst - ySrc) * step
		);
	}

	public static Vector2f Multiply(Vector2f vec, float value)
	{
		return new Vector2f(vec.x * value, vec.y * value);
	}

	public static Vector2f Multiply(Vector2f vec, Vector2f value)
	{
		return new Vector2f(vec.x * value.x, vec.y * value.y);
	}
}
