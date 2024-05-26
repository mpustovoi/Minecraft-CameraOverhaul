package mirsario.cameraoverhaul.utilities;

import org.joml.Vector2f;
import org.joml.Vector3f;

public final class VectorUtils {
	#if MC_RELEASE
	public static Vector2f toJoml(net.minecraft.world.phys.Vec2 vec) {
		return new Vector2f((float)vec.x, (float)vec.y);
	}
	public static Vector3f toJoml(net.minecraft.world.phys.Vec3 vec) {
		return new Vector3f((float)vec.x, (float)vec.y, (float)vec.z);
	}
	#endif

	public static float length(Vector2f vec) {
		return length(vec.x, vec.y);
	}
	public static float length(float x, float y) {
		return (float)Math.sqrt(x * x + y * y);
	}

	public static Vector2f rotate(Vector2f vec, float degrees) {
		double radians = Math.toRadians(degrees);
		float sin = (float)Math.sin(radians);
		float cos = (float)Math.cos(radians);

		return new Vector2f((cos * vec.x) - (sin * vec.y), (sin * vec.x) + (cos * vec.y));
	}

	public static Vector2f lerp(Vector2f src, Vector2f dst, float step) {
		return lerp(src.x, src.y, dst.x, dst.y, step);
	}
	public static Vector2f lerp(float xSrc, float ySrc, float xDst, float yDst, float step) {
		return new Vector2f(
			xSrc + (xDst - xSrc) * step,
			ySrc + (yDst - ySrc) * step
		);
	}

	public static Vector2f multiply(Vector2f vec, float value) {
		return new Vector2f(vec.x * value, vec.y * value);
	}
	public static Vector2f multiply(Vector2f vec, Vector2f value) {
		return new Vector2f(vec.x * value.x, vec.y * value.y);
	}
}
