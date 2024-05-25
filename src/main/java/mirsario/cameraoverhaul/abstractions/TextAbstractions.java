package mirsario.cameraoverhaul.abstractions;

#if MC_RELEASE
	import net.minecraft.network.chat.*;
#endif

public final class TextAbstractions
{
	#if MC_RELEASE
	public static Component CreateText(String key)
	{
		#if MC_VERSION >= "11900"
			return Component.translatable(key);
		#else
			return new TranslatableComponent(key);
		#endif
	}
	#endif
}
