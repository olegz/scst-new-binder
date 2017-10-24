package org.springframework.cloud.stream.binding.support;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
/**
 *
 * @author Oleg Zhurakousky
 *
 */
class BinderEnvironmentSetup implements EnvironmentPostProcessor {

	private final static Method addUrl;
	static {
		addUrl = ReflectionUtils.findMethod(URLClassLoader.class, "addURL", URL.class);
		addUrl.setAccessible(true);
	}

	public Set<String> bootJarDependencyNames = new HashSet<>();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String binderUrl = environment.getProperty("binder.binder-url");
		if (!StringUtils.hasText(binderUrl)){
			return;
		}
		try {
			File binderUrlFile = new File(new URI(binderUrl).getPath());
			Assert.isTrue(binderUrlFile.exists(), "Failed to resolve binder URL: " + binderUrlFile);
			JarFileArchive bootArchive = new JarFileArchive(binderUrlFile);
			List<Archive> bootArchives = new ArrayList<>(bootArchive.getNestedArchives(x -> isNestedArchive(x)));
			List<URL> providedDependenciesUrls = new ArrayList<>();
			providedDependenciesUrls.add(new URL(binderUrl));
			for (Archive arch : bootArchives) {
				providedDependenciesUrls.add(arch.getUrl());
			}

			this.clearSpringFactoriesLoadersCache();

			this.updateAppClassLoader((URLClassLoader) application.getClassLoader(), providedDependenciesUrls);

			Stream.of(((URLClassLoader) application.getClassLoader()).getURLs()).forEach(System.out::println);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to add '" + binderUrl + "' to classpath.", e);
		}
	}

	//TODO talk to Boot team if there is a way to do it cleanly
	private void clearSpringFactoriesLoadersCache() throws Exception {
		Field field = ReflectionUtils.findField(SpringFactoriesLoader.class, "cache");
		field.setAccessible(true);
		Map<?, ?> cache = (Map<?, ?>) field.get(null);
		cache.clear();
	}

	private void updateAppClassLoader(URLClassLoader appClassLoader, List<URL> providedDependenciesUrls) {

		providedDependenciesUrls.stream().forEach(url -> ReflectionUtils.invokeMethod(addUrl, appClassLoader, url));

		Iterator<URL> appClassLoaderUrlIter = Arrays.asList(appClassLoader.getURLs()).iterator();
		while (appClassLoaderUrlIter.hasNext()) {
			String appClassLoaderUrl = appClassLoaderUrlIter.next().toString();
			for (String bootJarDependencyName : this.bootJarDependencyNames) {
				if (appClassLoaderUrl.contains(bootJarDependencyName)){
					Iterator<URL> providedDependenciesUrlsIter = providedDependenciesUrls.iterator();
					while(providedDependenciesUrlsIter.hasNext()) {
						String url = providedDependenciesUrlsIter.next().toString();
						if (url.contains(bootJarDependencyName)){
							providedDependenciesUrlsIter.remove();
						}
					}
				}
			}
		}
	}

	private boolean isNestedArchive(Archive.Entry entry) {
		System.out.println(entry.getName());
		if (entry.getName().endsWith(".jar")){
			this.bootJarDependencyNames.add(entry.getName().replaceAll("BOOT-INF/lib/", ""));
		}
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	}
}
