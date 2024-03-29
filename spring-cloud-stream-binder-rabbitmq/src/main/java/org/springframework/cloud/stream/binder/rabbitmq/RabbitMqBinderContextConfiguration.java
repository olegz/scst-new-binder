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
package org.springframework.cloud.stream.binder.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqCommonProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqConsumerProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqProducerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RabbitMqProducerProperties.class, RabbitMqConsumerProperties.class, RabbitMqCommonProperties.class})
public class RabbitMqBinderContextConfiguration {

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	public RabbitMqBindingFactory rabbitMqBindingFactory() {
		return new RabbitMqBindingFactory();
	}

	@Bean
	public RabbitMqExchangeQueueProvisioner rabbitMqDestinationProvisioner() {
		return new RabbitMqExchangeQueueProvisioner(this.connectionFactory);
	}
}
