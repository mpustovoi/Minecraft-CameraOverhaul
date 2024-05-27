package mirsario.cameraoverhaul.abstractions;

#if MC_RELEASE
import com.mojang.math.*;
import net.minecraft.world.phys.*;
#if MC_VERSION >= "11500"
import com.mojang.blaze3d.vertex.*;
#endif
#if MC_VERSION >= "11903"
import org.joml.*;
#endif
#endif

public final class MathAbstractions {
#if MC_RELEASE && MC_VERSION >= "11500"
	public static void rotateMatrixByAxis(com.mojang.blaze3d.vertex.PoseStack matrix, double axisX, double axisY, double axisZ, double rotation) {
#if MC_VERSION >= "11903"
		matrix.mulPose(Axis.of(new Vector3f((float)axisX, (float)axisY, (float)axisZ)).rotationDegrees((float)rotation));
#else
		matrix.mulPose(new Vector3f((float)axisX, (float)axisY, (float)axisZ).rotationDegrees((float)rotation));
#endif
	}
#endif
}
