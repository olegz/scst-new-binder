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

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqCommonProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqConsumerBinding;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqConsumerProperties;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqProducerBinding;
import org.springframework.cloud.stream.binder.rabbitmq.support.RabbitMqProducerProperties;
import org.springframework.cloud.stream.binding.api.Binding;
import org.springframework.cloud.stream.binding.api.BindingFactory;
import org.springframework.stereotype.Component;

/**
 * @author Oleg Zhurakousky
 *
 */
//@Component

public class RabbitMqBindingFactory implements BindingFactory<RabbitMqProducerProperties, RabbitMqConsumerProperties> {

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private RabbitMqExchangeQueueProvisioner provisioner;

	@Override
	public <I, O> Binding bindConsumer(String consumerTargetBeanName, String group, Function<I, O> consumerTarget, RabbitMqConsumerProperties consumerProperties) {
		RabbitMqConsumerBinding<I,O> binding = new RabbitMqConsumerBinding<>(consumerTargetBeanName, group, consumerTarget,
				connectionFactory, provisioner, consumerProperties);
		return binding;
	}

	@Override
	public <O> Binding bindProducer(String name, Supplier<O> outboundBindTarget, RabbitMqProducerProperties producerProperties) {
		RabbitMqProducerBinding binding = new RabbitMqProducerBinding(name, outboundBindTarget, producerProperties);
		return binding;
	}

}
