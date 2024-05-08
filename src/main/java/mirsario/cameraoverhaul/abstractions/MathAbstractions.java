package mirsario.cameraoverhaul.abstractions;

import com.mojang.math.*;
import net.minecraft.world.phys.*;
#if MC_VERSION >= "11500"
import com.mojang.blaze3d.vertex.*;
#endif
#if MC_VERSION >= "11903"
import org.joml.*;
#endif

public final class MathAbstractions
{
#if MC_VERSION >= "11500"
	public static void RotateMatrixByAxis(com.mojang.blaze3d.vertex.PoseStack matrix, float axisX, float axisY, float axisZ, float rotation) {
#if MC_VERSION >= "11903"
		matrix.mulPose(Axis.of(new Vector3f(axisX, axisY, axisZ)).rotationDegrees(rotation));
#else
	    matrix.mulPose(new Vector3f(axisX, axisY, axisZ).rotationDegrees(rotation));
#endif
	}
#endif
}
