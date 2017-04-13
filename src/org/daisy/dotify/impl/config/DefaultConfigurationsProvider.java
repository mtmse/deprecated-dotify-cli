package org.daisy.dotify.impl.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.config.ConfigurationDetails;
import org.daisy.dotify.api.config.ConfigurationsProvider;
import org.daisy.dotify.api.config.ConfigurationsProviderException;
import org.daisy.dotify.common.io.AbstractResourceLocator;
import org.daisy.dotify.common.io.ResourceLocatorException;

/**
 * Provides a default set of configurations. These should not be accessed directly,
 * use the ConfigurationsCatalog instead. 
 * @author Joel Håkansson
 */
public class DefaultConfigurationsProvider extends AbstractResourceLocator implements ConfigurationsProvider {
	private static final String PRESETS_PATH = "resource-files/";
	private final Logger logger;
	private final Properties props = new Properties();
	private final Map<String, String> urls;
	private Set<ConfigurationDetails> details;
	
	/**
	 * Creates a new default configurations provider. This should not be accessed directly,
	 * use the ConfigurationsCatalog instead.
	 */
	public DefaultConfigurationsProvider() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = getResource("presets_catalog.xml");
	        if(tablesURL!=null){
	        	props.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
		urls = new HashMap<String, String>();
		for (Entry<Object, Object> e : props.entrySet()) {
			urls.put(e.getKey().toString(), PRESETS_PATH + e.getValue());
		}
	}

	@Override
	public Set<ConfigurationDetails> getConfigurationDetails() {
		if (details == null) {
			details = new HashSet<>();
			Properties descs = new Properties();
			try {
				URL url = getResource("presets_descriptions.xml");
				descs.loadFromXML(url.openStream());
				for (String key : urls.keySet()) {
					details.add(
							new ConfigurationDetails.Builder(key)
							.description(descs.getProperty(key).replaceAll("\\s+", " "))
							.build()
					);					
				}
			} catch (ResourceLocatorException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			} catch (InvalidPropertiesFormatException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			} catch (IOException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			}
		}
		return details;
	}

	private URL getConfigurationURL(String identifier) throws ConfigurationsProviderException {
		try {
			return this.getResource(urls.get(identifier));
		} catch (ResourceLocatorException e) {
			throw new ConfigurationsProviderException(e);
		}
	}

	@Override
	public Map<String, Object> getConfiguration(String identifier) throws ConfigurationsProviderException {
		Properties p = new Properties();
		URL configURL = getConfigurationURL(identifier);
		try {
			p.loadFromXML(configURL.openStream());
		} catch (FileNotFoundException e) {
			throw new ConfigurationsProviderException("Configuration file not found: " + configURL, e);
		} catch (InvalidPropertiesFormatException e) {
			throw new ConfigurationsProviderException("Configuration file could not be parsed: " + configURL, e);
		} catch (IOException e) {
			throw new ConfigurationsProviderException("IOException while reading configuration file: " + configURL, e);
		}
		Map<String, Object> ret = new HashMap<>();
		for (Object key : p.keySet()) {
			ret.put(key.toString(), p.get(key));
		}
		return ret;
	}

}