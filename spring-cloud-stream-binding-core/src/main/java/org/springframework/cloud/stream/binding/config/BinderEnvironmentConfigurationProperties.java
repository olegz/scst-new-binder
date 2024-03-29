/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.stream.binding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Oleg Zhurakousky
 */
@ConfigurationProperties(BinderEnvironmentConfigurationProperties.PREFIX)
public class BinderEnvironmentConfigurationProperties {

	public final static String PREFIX = "binder";

	public final static String BINDER_URL = PREFIX + ".binder-url";

	/**
	 * Binder URL. Could be maven or file
	 */
	private String binderUrl;

	public String getBinderUrl() {
		return binderUrl;
	}

	public void setBinderUrl(String binderUrl) {
		this.binderUrl = binderUrl;
	}
}
