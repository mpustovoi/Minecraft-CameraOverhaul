//? if FABRIC_LOADER && MC_RELEASE
package mirsario.cameraoverhaul.fabric;

import java.lang.reflect.Field;
import java.util.function.*;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.clothconfig2.gui.entries.*;
import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;
import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;

// Beyond annoying.
//? if <=1.17 {
/*import io.github.prospector.modmenu.api.*;
import net.minecraft.client.gui.screens.*;
*///?} else {
import com.terraformersmc.modmenu.api.*;
//?}

@SuppressWarnings({ "deprecation", "unused" })
public class ModMenuConfigIntegration implements ModMenuApi {
	private static final String CONFIG_ENTRIES_PREFIX = "cameraoverhaul.config";

//? if <=1.17 {
	/*public String getModId() {
		return CameraOverhaul.MOD_ID;
	}

	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> getConfigBuilder().build();
	}
*///?}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> getConfigBuilder().build();
	}

	public static ConfigBuilder getConfigBuilder() {
		CameraOverhaul.LOGGER.info("Opening config screen.");
		var config = Configuration.get();
		var configDefault = Configuration.getDefault();

		var builder = (ConfigBuilder.create()
			.setParentScreen(Minecraft.getInstance().screen)
			.setTitle(getText("cameraoverhaul.config.title"))
			.transparentBackground()
			.setSavingRunnable(Configuration::saveConfig)
		);

		var general = builder.getOrCreateCategory(getText("cameraoverhaul.config.category.general"));
		var entryBuilder = builder.entryBuilder();
		var configClass = ConfigData.class;

		for (var field : configClass.getFields()) {
			var baseTranslationPath = CONFIG_ENTRIES_PREFIX + "." + field.getName().toLowerCase();
			AbstractConfigListEntry<?> entry = null;

			try {
				if (field.getType() == float.class) {
					entry = (entryBuilder.startFloatField(getText(baseTranslationPath + ".name"), (float)field.get(config))
						.setDefaultValue((float)field.get(configDefault))
						.setTooltip(getText(baseTranslationPath + ".tooltip"))
						.setSaveConsumer(newValue -> trySetConfigFieldValue(field, newValue))
						.build()
					);
				} else if (field.getType() == boolean.class) {
					entry = (entryBuilder.startBooleanToggle(getText(baseTranslationPath + ".name"), (boolean)field.get(config))
						.setDefaultValue((boolean)field.get(configDefault))
						.setTooltip(getText(baseTranslationPath + ".tooltip"))
						.setSaveConsumer(newValue -> trySetConfigFieldValue(field, newValue))
						.build()
					);
				}
			}
			catch (Exception e) { CameraOverhaul.LOGGER.trace(e); }

			if (entry != null)
				general.addEntry(entry);
		}

		return builder;
	}

	private static float clampSmoothness(float value) {
		return MathUtils.clamp(value, 0f, 0.999f);
	}

	// Entry Helpers

	public static BooleanListEntry createBooleanEntry(ConfigEntryBuilder entryBuilder, String entryName, Boolean defaultValue, Boolean value, Function<Boolean, Boolean> setter) {
		var baseTranslationPath = CONFIG_ENTRIES_PREFIX + "." + entryName.toLowerCase();

		return entryBuilder.startBooleanToggle(getText(baseTranslationPath + ".name"), value)
			.setDefaultValue(defaultValue)
			.setTooltip(getText(baseTranslationPath + ".tooltip"))
			.setSaveConsumer(setter::apply)
			.build();
	}

	public static FloatListEntry createFloatFactorEntry(ConfigEntryBuilder entryBuilder, String entryName, float defaultValue, float value, Function<Float, Float> setter) {
		var baseTranslationPath = CONFIG_ENTRIES_PREFIX + "." + entryName.toLowerCase();

		return entryBuilder.startFloatField(getText(baseTranslationPath + ".name"), value)
			.setDefaultValue(defaultValue)
			.setTooltip(getText(baseTranslationPath + ".tooltip"))
			.setSaveConsumer(setter::apply)
			.build();
	}

//? if >=1.17 {
	private static net.minecraft.network.chat.Component getText(String key) {
		return TextAbstractions.createText(key);
	}
//?} else {
	/*private static String getText(String key) {
		return TextAbstractions.createText(key).getString();
	}
*///?}

	private static void trySetConfigFieldValue(Field field, Object value) {
		try { field.set(Configuration.get(), value); }
		catch (Exception e) { CameraOverhaul.LOGGER.trace(e); }
	}
}
//#endif