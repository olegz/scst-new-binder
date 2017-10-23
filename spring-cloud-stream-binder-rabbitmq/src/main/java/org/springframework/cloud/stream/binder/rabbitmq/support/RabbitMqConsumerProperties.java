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
package org.springframework.cloud.stream.binder.rabbitmq.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binding.config.BinderEnvironmentConfigurationProperties;
import org.springframework.cloud.stream.binding.support.ConsumerProperties;

@ConfigurationProperties(BinderEnvironmentConfigurationProperties.PREFIX + ".consumer.rabbit")
public class RabbitMqConsumerProperties extends ConsumerProperties{

	/**
	 * Target consumer property
	 */
	private String targetConsumerProperty;

	public String getTargetConsumerProperty() {
		return targetConsumerProperty;
	}

	public void setTargetConsumerProperty(String targetConsumerProperty) {
		this.targetConsumerProperty = targetConsumerProperty;
	}
}
