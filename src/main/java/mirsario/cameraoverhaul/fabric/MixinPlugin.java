#if FABRIC_LOADER
package mirsario.cameraoverhaul.fabric;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;
import mirsario.cameraoverhaul.*;

import java.util.*;

public class MixinPlugin implements IMixinConfigPlugin
{
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if (!mixinClassName.startsWith("mirsario.")) {
			return true;
		}

		try {
			Class.forName(mixinClassName, false, getClass().getClassLoader());
			CameraOverhaul.Logger.info("CameraOverhaul: Applying present mixin: '" + mixinClassName + "'.");
			return true;
		}
		catch (ClassNotFoundException ignored) {
			CameraOverhaul.Logger.info("CameraOverhaul: Skipping missing mixin: '" + mixinClassName + "'.");
			return false;
		}
	}

	@Override
	public void onLoad(String mixinPackage) { }

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

	@Override
	public String getRefMapperConfig()
	{
		return null;
	}

	@Override
	public List<String> getMixins()
	{
		return null;
	}
}
#endif