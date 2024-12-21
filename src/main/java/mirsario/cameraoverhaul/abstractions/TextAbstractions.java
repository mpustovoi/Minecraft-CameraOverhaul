// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.abstractions;

//? if MC_RELEASE {
import net.minecraft.network.chat.*;

public final class TextAbstractions {
	public static Component createText(String key) {
		//? if >=1.19 {
			return Component.translatable(key);
		//?} else
			/*return new TranslatableComponent(key);*/
	}

	public static String getTextValue(String key) { return TextAbstractions.createText(key).getString(); }
	//? if >=1.17 {
		public static net.minecraft.network.chat.Component getText(String key) { return TextAbstractions.createText(key); }
	//?} else
		/*public static String getText(String key) { return TextAbstractions.createText(key).getString(); }*/
}
//?}
