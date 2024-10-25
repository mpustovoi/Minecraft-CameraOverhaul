//? if FABRIC_LOADER && MC_RELEASE
package mirsario.cameraoverhaul.fabric;

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
	
	@SuppressWarnings("resource") // MinecraftClient.getInstance() isn't a resource
	public static ConfigBuilder getConfigBuilder() {
		CameraOverhaul.LOGGER.info("Opening config screen.");
		ConfigData config = CameraOverhaul.instance.config;
		ConfigBuilder builder = ConfigBuilder.create()

			.setParentScreen(Minecraft.getInstance().screen)
			.setTitle(getText("cameraoverhaul.config.title"))
			.transparentBackground()
			.setSavingRunnable(() -> Configuration.saveConfig(CameraOverhaul.instance.config, CameraOverhaul.MOD_ID, ConfigData.CONFIG_VERSION));
		
		ConfigCategory general = builder.getOrCreateCategory(getText("cameraoverhaul.config.category.general"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		// Entries
		general.addEntry(createBooleanEntry(entryBuilder, "enabled", true, config.enabled, value -> config.enabled = value));
		// Roll factors
		general.addEntry(createFloatFactorEntry(entryBuilder, "strafingRollFactor", 1.0f, config.strafingRollFactor, value -> config.strafingRollFactor = value));
		general.addEntry(createFloatFactorEntry(entryBuilder, "strafingRollFactorWhenFlying", -1.0f, config.strafingRollFactorWhenFlying, value -> config.strafingRollFactorWhenFlying = value));
		general.addEntry(createFloatFactorEntry(entryBuilder, "strafingRollFactorWhenSwimming", -1.0f, config.strafingRollFactorWhenSwimming, value -> config.strafingRollFactorWhenSwimming = value));
		general.addEntry(createFloatFactorEntry(entryBuilder, "yawDeltaRollFactor", 1.0f, config.yawDeltaRollFactor, value -> config.yawDeltaRollFactor = value));
		// Pitch factors
		general.addEntry(createFloatFactorEntry(entryBuilder, "verticalVelocityPitchFactor", 1.0f, config.verticalVelocityPitchFactor, value -> config.verticalVelocityPitchFactor = value));
		general.addEntry(createFloatFactorEntry(entryBuilder, "forwardVelocityPitchFactor", 1.0f, config.forwardVelocityPitchFactor, value -> config.forwardVelocityPitchFactor = value));
		
		// Smoothing factors
		general.addEntry(createFloatFactorEntry(entryBuilder, "horizontalVelocitySmoothingFactor", 0.8f, clampSmoothness(config.horizontalVelocitySmoothingFactor), value -> config.horizontalVelocitySmoothingFactor = clampSmoothness(value)));
		general.addEntry(createFloatFactorEntry(entryBuilder, "verticalVelocitySmoothingFactor", 0.8f, clampSmoothness(config.verticalVelocitySmoothingFactor), value -> config.verticalVelocitySmoothingFactor = clampSmoothness(value)));
		general.addEntry(createFloatFactorEntry(entryBuilder, "yawDeltaSmoothingFactor", 0.8f, clampSmoothness(config.yawDeltaSmoothingFactor), value -> config.yawDeltaSmoothingFactor = clampSmoothness(value)));
		general.addEntry(createFloatFactorEntry(entryBuilder, "yawDeltaDecayFactor", 0.5f, clampSmoothness(config.yawDeltaDecayFactor), value -> config.yawDeltaDecayFactor = clampSmoothness(value)));
		
		return builder;
	}

	private static float clampSmoothness(float value) {
		return MathUtils.clamp(value, 0f, 0.999f);
	}

	// Entry Helpers

	public static BooleanListEntry createBooleanEntry(ConfigEntryBuilder entryBuilder, String entryName, Boolean defaultValue, Boolean value, Function<Boolean, Boolean> setter) {
		String lowerCaseName = entryName.toLowerCase();
		String baseTranslationPath = CONFIG_ENTRIES_PREFIX + "." + lowerCaseName;

		return entryBuilder.startBooleanToggle(getText(baseTranslationPath + ".name"), value)
			.setDefaultValue(defaultValue)
			.setTooltip(getText(baseTranslationPath + ".tooltip"))
			.setSaveConsumer(setter::apply)
			.build();
	}

	public static FloatListEntry createFloatFactorEntry(ConfigEntryBuilder entryBuilder, String entryName, float defaultValue, float value, Function<Float, Float> setter) {
		String lowerCaseName = entryName.toLowerCase();
		String baseTranslationPath = CONFIG_ENTRIES_PREFIX + "." + lowerCaseName;

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
}
//#endif