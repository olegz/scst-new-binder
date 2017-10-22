package org.springframework.cloud.stream.binding.support;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
/**
 *
 * @author Oleg Zhurakousky
 *
 */
class BinderEnvironmentSetup implements EnvironmentPostProcessor {

	private final Logger logger = LoggerFactory.getLogger(BinderEnvironmentSetup.class);

	public BinderEnvironmentSetup() {
		System.out.println("Creating BinderShellConfiguration");
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String binderUrl = environment.getProperty("binder.binder-url");
		if (logger.isTraceEnabled()){
			logger.trace("Adding: " + binderUrl + " to Application's classpath.");
		}
		try {
			File binderUrlFile = new File(new URI(binderUrl).getPath());
			Assert.isTrue(binderUrlFile.exists(), "Failed to resolve binder URL: " + binderUrlFile);
			JarFileArchive bootArchive = new JarFileArchive(binderUrlFile);
			List<Archive> bootArchives = new ArrayList<>(bootArchive.getNestedArchives(x -> isNestedArchive(x)));
			if (logger.isTraceEnabled()){
				logger.trace("Adding to the classpath: " + bootArchives);
			}

			List<URL> urls = new ArrayList<>();
			urls.add(new URL(binderUrl));
			for (Archive ba : bootArchives) {
				urls.add(ba.getUrl());
			}
			LaunchedURLClassLoader lcl = new LaunchedURLClassLoader(urls.toArray(new URL[]{}), application.getClassLoader());
			Thread.currentThread().setContextClassLoader(lcl);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to add '" + binderUrl + "' to classpath.", e);
		}
	}

	private boolean isNestedArchive(Archive.Entry entry) {
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	}
}
