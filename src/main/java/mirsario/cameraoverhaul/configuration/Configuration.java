package mirsario.cameraoverhaul.configuration;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import mirsario.cameraoverhaul.*;
import net.fabricmc.loader.api.*;

public final class Configuration {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();

	public static <T extends BaseConfigData> T loadConfig(Class<T> tClass, String configName, int configVersion) {
		T configData = null;
		Path configFile = CONFIG_PATH.resolve(configName + ".json");
		boolean saveConfig = false;
		
		try {
			Files.createDirectories(CONFIG_PATH);

			if (Files.exists(configFile)) {
				BufferedReader fileReader = Files.newBufferedReader(configFile);
				configData = GSON.fromJson(fileReader, tClass);
				fileReader.close();
				
				// Save the config on first runs of new versions.
				if (configData.configVersion < configVersion) {
					saveConfig = true;
				}
			} else {
				configData = (T)tClass.getDeclaredConstructor().newInstance();
				saveConfig = true;
			}
		} catch(Exception e) {
			CameraOverhaul.LOGGER.error("Error when initializing config", e);
		}
		
		if (saveConfig) {
			saveConfig(configData, configName, configVersion);
		}
		
		return configData;
	}
	
	public static <T extends BaseConfigData> void saveConfig(T configData, String configName, int configVersion) {
		Path configFile = CONFIG_PATH.resolve(configName+".json");
		
		configData.configVersion = configVersion;
		
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)){
			writer.write(GSON.toJson(configData));
		} catch (IOException e) {
			CameraOverhaul.LOGGER.error("Couldn't save config file", e);
		}
	}
}