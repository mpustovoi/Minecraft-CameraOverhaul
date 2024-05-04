package mirsario.cameraoverhaul.abstractions;

import net.minecraft.text.*;

public final class TextAbstractions
{
	public static Text CreateText(String key)
	{
#if MC_VERSION >= "11900"
		return Text.translatable(key);
#else
		return new TranslatableText(key);
#endif
	}
}
