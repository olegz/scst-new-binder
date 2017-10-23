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

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binding.support.AbstractBinding;

public class RabbitMqConsumerBinding extends AbstractBinding  {

	private Logger logger = LoggerFactory.getLogger(RabbitMqConsumerBinding.class);

	public <I,O> RabbitMqConsumerBinding(String targetBeanName, String group, Function<I, O> inboundBindTarget, RabbitMqConsumerProperties consumerProperties) {
		super(targetBeanName);
	}

	@Override
	public void doStart() {
		if (logger.isInfoEnabled()) {
			logger.info("Starting binding: " + this.getName());
		}
	}

	@Override
	public void doStop() {
		System.out.println("####### STOPPING");
		if (logger.isInfoEnabled()) {
			logger.info("Stopping binding: " + this.getName());
		}
	}
}
