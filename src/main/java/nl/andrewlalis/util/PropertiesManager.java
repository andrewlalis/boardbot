package nl.andrewlalis.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class which manages access to application properties by maintaining
 * one properties object which can be accessed throughout the rest of the app.
 */
public class PropertiesManager {
	private static final String APP_PROPERTIES_FILE = "app.properties";

	/**
	 * The single instance of this class that exists at runtime.
	 */
	private static PropertiesManager instance;

	/**
	 * @return The single instance of the properties manager.
	 */
	public static PropertiesManager getInstance() {
		if (instance == null) {
			try {
				instance = new PropertiesManager(APP_PROPERTIES_FILE);
			} catch (IOException e) {
				throw new RuntimeException("Could not construct properties manager.", e);
			}
		}
		return instance;
	}

	/**
	 * The set of properties that this manager handles.
	 */
	private final Properties properties;

	/**
	 * Constructs the manager, and in doing so initializes the single instance's
	 * properties with the values found in the given file.
	 * @param propertiesFileName The name of the file to load properties from.
	 * @throws IOException If we could not read the file or load it into the
	 * properties.
	 */
	public PropertiesManager(String propertiesFileName) throws IOException {
		this.properties = new Properties();
		final var inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
		if (inputStream == null) {
			throw new IOException("Could not obtain input stream for properties file: " + propertiesFileName);
		}
		this.properties.load(inputStream);
		inputStream.close();
	}

	/**
	 * Proxy to {@link Properties#getProperty(String)}.
	 * @param key The key of the property to get.
	 * @return The value of the property with the given key.
	 */
	public String get(String key) {
		return this.properties.getProperty(key);
	}

	/**
	 * Proxy to {@link Properties#getProperty(String, String)}.
	 * @param key The key of the property to get.
	 * @param defaultValue The default value to return if no value was found for
	 *                     the given key.
	 * @return The value of the property with the given key.
	 */
	public String get(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}
}
