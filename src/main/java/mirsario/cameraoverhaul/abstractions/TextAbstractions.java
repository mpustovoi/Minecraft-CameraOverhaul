package mirsario.cameraoverhaul.abstractions;

import net.minecraft.network.chat.*;

public final class TextAbstractions
{
	public static Component CreateText(String key)
	{
#if MC_VERSION >= "11900"
		return Component.translatable(key);
#else
		return new TranslatableComponent(key);
#endif
	}
}
