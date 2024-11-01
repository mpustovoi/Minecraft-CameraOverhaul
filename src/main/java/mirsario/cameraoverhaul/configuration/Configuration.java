package mirsario.cameraoverhaul.configuration;

import com.moandjiezana.toml.*;
import java.io.*;
import java.nio.file.*;
import mirsario.cameraoverhaul.*;
import net.fabricmc.loader.api.*;

public final class Configuration {
	private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
	private static final Path CONFIG_PATH = CONFIG_DIR.resolve(CameraOverhaul.MOD_ID + ".toml");
	private static final Toml TOML = new Toml();
	private static final ConfigData configDefault = new ConfigData();
	private static ConfigData configCurrent;

	public static ConfigData get() { return configCurrent; }
	public static ConfigData getDefault() { return configDefault; }

	public static void loadConfig() {
		var file = CONFIG_PATH.toFile();

		try {
			Files.createDirectories(CONFIG_DIR);

			if (file.exists()) {
				configCurrent = TOML.read(file).to(ConfigData.class);
			} else {
				configCurrent = new ConfigData();
			}
		} catch (Exception e) {
			configCurrent = new ConfigData();
			CameraOverhaul.LOGGER.error("Failed to load config file", e);
		}

		saveConfig();
	}
	
	public static void saveConfig() {
		configCurrent.configVersion = ConfigData.CONFIG_VERSION;

		try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
			new TomlWriter().write(configCurrent, writer);
		} catch (IOException e) {
			CameraOverhaul.LOGGER.error("Failed to save config file", e);
		}
	}
}