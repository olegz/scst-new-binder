package org.springframework.cloud.stream.binding.support;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.core.env.ConfigurableEnvironment;
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
			List<URL> providedDependencyUrls = bootArchives.stream().map(arch -> doGetURL(arch)).collect(Collectors.toList());

			URLClassLoader appClassLoader = (URLClassLoader)application.getClassLoader();

			this.filterOutExistingDependencies(appClassLoader, providedDependencyUrls);
			providedDependencyUrls.stream().forEach(url -> ReflectionUtils.invokeMethod(addUrl, appClassLoader, url));

			//Stream.of(appClassLoader.getURLs()).forEach(System.out::println);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to add '" + binderUrl + "' to classpath.", e);
		}
	}

	private URL doGetURL(Archive arch) {
		try {
			return arch.getUrl();
		}
		catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	private void filterOutExistingDependencies(URLClassLoader appLoader, List<URL> providedDependenciesUrls) {
		Iterator<URL> appClassLoaderUrlIter = Arrays.asList(appLoader.getURLs()).iterator();
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
		if (entry.getName().endsWith(".jar")){
			this.bootJarDependencyNames.add(entry.getName().replaceAll("BOOT-INF/lib/", ""));
		}
		if (entry.isDirectory()) {
			return entry.getName().equals("BOOT-INF/classes/");
		}
		return entry.getName().startsWith("BOOT-INF/lib/");
	}
}
