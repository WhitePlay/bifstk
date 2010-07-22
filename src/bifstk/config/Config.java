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

public class Config {

	private static Config instance = null;

	private File config = null;

	private Map<Property, String> properties = null;

	private Config(File conf) throws BifstkException {
		this.config = conf;
		this.properties = new HashMap<Property, String>();
		this.loadConfig();
	}

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

	public static String getValue(Property prop) {
		if (Config.instance == null)
			throw new IllegalStateException("Config was not loaded");

		return instance.properties.get(prop);
	}

	public static void load(String path) throws BifstkException {
		Config.instance = new Config(new File(path));
	}

}
