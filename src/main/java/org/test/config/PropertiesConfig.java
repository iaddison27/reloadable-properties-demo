package org.test.config;

import java.io.File;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesConfig {

	private static final Logger LOGGER = LogManager.getLogger(PropertiesConfig.class);

	// Default values for properties
	private static final int DEFAULT_RESULTS_COUNT = 10;
	private static final int DEFAULT_RESULT_LENGTH = 5;

	private File baseDir = new File(System.getProperty("catalina.base"), "conf");

	@Value("#{application['refreshDelay']}")
	private long refreshDelay;

	private PropertiesConfiguration configuration;

	@PostConstruct
	private void init() {
		try {
			final File configFile = new File(baseDir, "reloadable.properties");
			LOGGER.info("Loading configuration from {}.", configFile);
			if (!configFile.exists()) {
				LOGGER.warn("Properties file does not exist - create one to override defaults");
			}

			configuration = new PropertiesConfiguration(configFile);

			// Reload the configuration every time the underlying file is changed
			FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
			configuration.setReloadingStrategy(reloadingStrategy);
			// How often to check the file's last modified date (avoids permanent disc access)
			reloadingStrategy.setRefreshDelay(refreshDelay);

			// Perform some logging when configuration changes
			configuration.addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {

					if (LOGGER.isInfoEnabled()) {
						final StringBuilder builder = new StringBuilder("Configuration amended:");
						Iterator<String> keys = configuration.getKeys();
						while (keys.hasNext()) {
							final String key = keys.next();
							builder.append("\n").append(key).append("=").append(configuration.getProperty(key));
						}
						LOGGER.info(builder.toString());
					}
				}
			});
		} catch (ConfigurationException ex) {
			LOGGER.error("Failed to load properties file", ex);
		}
	}

	public final int getResultsCount() {
		return configuration.getInt("results.count", DEFAULT_RESULTS_COUNT);
	}

	public final int getIndividualResultLength() {
		return configuration.getInt("result.length", DEFAULT_RESULT_LENGTH);
	}

}
