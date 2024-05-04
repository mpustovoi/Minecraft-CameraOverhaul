package mirsario.cameraoverhaul.abstractions;

import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;
#if MC_VERSION >= "11903"
import org.joml.*;
#endif

public final class MathAbstractions
{
#if MC_VERSION >= "11500"
	public static void RotateMatrixByAxis(MatrixStack matrix, float axisX, float axisY, float axisZ, float rotation) {
#if MC_VERSION >= "11903"
		matrix.multiply(RotationAxis.of(new Vector3f(axisX, axisY, axisZ)).rotationDegrees(rotation));
#elif MC_VERSION >= "11605"
		matrix.multiply(new Vec3f(axisX, axisY, axisZ).getDegreesQuaternion(rotation));
#else
		matrix.multiply(new Vector3f(axisX, axisY, axisZ).getDegreesQuaternion(rotation));
#endif
	}
#endif
}
