package bifstk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import bifstk.BifstkException;

/**
 * Bifstk's Configuration facility
 * <p>
 * Access to configuration properties can be performed statically with
 * {@link #getValue(Property)} after an initial call to {@link #load(String)}
 * 
 * 
 */
public class Config {

	/** singleton instance */
	private static Config instance = null;

	/** configuration file */
	private File config = null;

	/** values for all {@link bifstk.config.Property} */
	private Map<Property, String> properties = null;

	/**
	 * Default constructor
	 * 
	 * @param conf path to the configuration file
	 * @throws BifstkException the configuration was not loaded
	 */
	private Config(File conf) throws BifstkException {
		this.config = conf;
		this.properties = new HashMap<Property, String>();
		this.loadConfig();
	}

	/**
	 * Loads the configuration from the file
	 * 
	 * @throws BifstkException the file could not be read, or it is incomplete
	 */
	private void loadConfig() throws BifstkException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(this.config);
		} catch (FileNotFoundException e) {
			throw new BifstkException("Could not find configuration file", e);
		}

		Properties props = new Properties();
		try {
			props.load(in);
			in.close();
		} catch (IOException e) {
			throw new BifstkException("Could not read configuration file", e);
		}

		try {
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				for (Property p : Property.values()) {
					if (p.getProperty().equals(key)) {
						properties.put(p, (String) entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			throw new BifstkException("Error parsing configuration file", e);
		}

		for (Property p : Property.values()) {
			if (!this.properties.containsKey(p)) {
				String message = "Property " + p + " (" + p.getProperty()
						+ ") is not defined in configuration";
				throw new BifstkException(message);
			}
		}
	}

	/**
	 * Returns as a String the value for a given configuration Property
	 * <p>
	 * It is the caller's responsibility to determine the final type of the
	 * returned String and do appropriate casting / parsing.
	 * 
	 * @param prop Name of the {@link bifstk.config.Property} to query
	 * @return value of the Property <code>prop</code>
	 */
	public static String getValue(Property prop) {
		if (Config.instance == null)
			throw new IllegalStateException("Config was not loaded");

		return instance.properties.get(prop);
	}

	/**
	 * Statically loads the configuration from a file
	 * <p>
	 * Will create the singleton instance and enable access to property values
	 * through {@link #getValue(Property)}
	 * 
	 * @param path path to a local file containing definitions for all
	 *            {@link bifstk.config.Property}
	 * @throws BifstkException the configuration was not loaded
	 */
	public static void load(String path) throws BifstkException {
		Config.instance = new Config(new File(path));
	}

}
