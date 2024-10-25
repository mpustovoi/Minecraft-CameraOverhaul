package mirsario.cameraoverhaul.abstractions;

//? if MC_RELEASE
import net.minecraft.network.chat.*;

public final class TextAbstractions {
//? if MC_RELEASE {
	public static Component createText(String key) {
		//? if >=1.19 {
			return Component.translatable(key);
		//?} else
			/*return new TranslatableComponent(key);*/
	}
//?}
}
