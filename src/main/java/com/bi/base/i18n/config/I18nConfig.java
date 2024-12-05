/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.i18n.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

/**
 * Internationalization language config.<br>
 * File naming '<code>XXX-XXX_<font color="red">language code</font>.properties<code/>'.
 * Use '<code>_<code/>' split language prefix, so customized file name must be not contain '<code>_<code/>' char<br>
 * The variable loading priority: i18n root > layer1 > layer2 > layerN...<br>
 * If find out variable in layer1, then same variable will not be loaded in layer2.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class I18nConfig {

	@Value("${i18n.messages.basename}")
	private String basename;

	@Value("${i18n.messages.cacheSeconds:-1}")
	private long cacheMillis;

	@Value("${i18n.messages.encoding}")
	private String encoding;

	@Bean
	public MessageSource messageSource() throws IOException {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		Set<String> baseNames = getProperties(basename);
		messageSource.setBasenames(baseNames.toArray(new String[0]));
		messageSource.setDefaultEncoding(encoding);
		messageSource.setCacheMillis(cacheMillis);
		return messageSource;
	}

	/**
	 * Get all internationalization properties file.
	 *
	 * @param locationPattern file location pattern
	 * @return file names
	 * @throws IOException
	 */
	private Set<String> getProperties(String locationPattern) throws IOException {
		Set<String> set = new HashSet<>();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver.getResources(locationPattern);
		for (Resource resource : resources) {
			String url = resource.getURL().toString();
			int lastIndex = url.lastIndexOf("/");
			String prefix = url.substring(0, lastIndex + 1);
			String suffix = url.substring(lastIndex + 1);
			log.debug(url);
			suffix = suffix.split("\\.")[0].split("_")[0];
			set.add(prefix + suffix);
		}
		return set;
	}

}
