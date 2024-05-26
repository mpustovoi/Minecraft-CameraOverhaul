#if FABRIC_LOADER
package mirsario.cameraoverhaul.fabric;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;
import mirsario.cameraoverhaul.*;
import java.util.*;

public class MixinPlugin implements IMixinConfigPlugin {
    private static final String MIXINS_PACKAGE = "mirsario.cameraoverhaul.fabric.mixins";
    private static final String[] MIXINS = new String[] {
        "CameraMixin",
        "CameraMixinBta",
        "GameRendererMixin",
    };

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
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
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
        var list = new ArrayList<String>();

        for (String mixin : MIXINS) {
            String mixinClassName = MIXINS_PACKAGE + "." + mixin;
            try {
                Class.forName(mixinClassName, false, getClass().getClassLoader());
                CameraOverhaul.LOGGER.info("Applying present mixin: '" + mixinClassName + "'.");
                list.add(mixin);
            } catch (ClassNotFoundException ignored) {
                CameraOverhaul.LOGGER.info("Skipping missing mixin: '" + mixinClassName + "'.");
            }
        }

		return list;
	}
}
#endif