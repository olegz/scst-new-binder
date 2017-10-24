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

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.postprocessor.DelegatingDecompressingPostProcessor;
import org.springframework.cloud.stream.binder.rabbitmq.RabbitMqExchangeQueueProvisioner;
import org.springframework.cloud.stream.binding.support.AbstractBinding;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class RabbitMqConsumerBinding<I,O> extends AbstractBinding  {

	private static final MessagePropertiesConverter INBOUN_MESSAGE_PROPERTIES_CONVERTER =
			new DefaultMessagePropertiesConverter() {

				@Override
				public MessageProperties toMessageProperties(AMQP.BasicProperties source, Envelope envelope,
						String charset) {
					MessageProperties properties = super.toMessageProperties(source, envelope, charset);
					properties.setDeliveryMode(null);
					return properties;
				}

			};

	private Logger logger = LoggerFactory.getLogger(RabbitMqConsumerBinding.class);

	private final RabbitMqConsumerProperties consumerProperties;

	private final Function<I, O> consumerTarget;

	private final ConnectionFactory connectionFactory;

	private final RabbitMqExchangeQueueProvisioner provisioner;

	private final String group;

	private final String consumerTargetBeanName;

	private final MessagePostProcessor decompressingPostProcessor = new DelegatingDecompressingPostProcessor();

	public RabbitMqConsumerBinding(String consumerTargetBeanName, String group,
			Function<I, O> consumerTarget,
			ConnectionFactory connectionFactory,
			RabbitMqExchangeQueueProvisioner provisioner,
			RabbitMqConsumerProperties consumerProperties) {
		super(consumerTargetBeanName);
		this.consumerProperties = consumerProperties;
		this.consumerTarget = consumerTarget;
		this.connectionFactory = connectionFactory;
		this.provisioner = provisioner;
		this.consumerTargetBeanName = consumerTargetBeanName;
		this.group = group;
	}

	@Override
	public void doStart() {
		if (logger.isInfoEnabled()) {
			logger.info("Starting binding: " + this.getName());
		}

		String queueName = this.provisioner.provisionConsumerDestination(this.consumerTargetBeanName, group, this.consumerProperties);
		/*
		 * Create MessageListeningContainer
		 * Start it
		 */
		//String destination = this.getName();
		SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(
				this.connectionFactory);
		listenerContainer.setAcknowledgeMode(this.consumerProperties.getAcknowledgeMode());
		listenerContainer.setChannelTransacted(this.consumerProperties.isTransacted());
		listenerContainer.setDefaultRequeueRejected(this.consumerProperties.isRequeueRejected());
		int concurrency = this.consumerProperties.getConcurrency();
		concurrency = concurrency > 0 ? concurrency : 1;
		listenerContainer.setConcurrentConsumers(concurrency);
		int maxConcurrency = this.consumerProperties.getMaxConcurrency();
		if (maxConcurrency > concurrency) {
			listenerContainer.setMaxConcurrentConsumers(maxConcurrency);
		}
		listenerContainer.setPrefetchCount(this.consumerProperties.getPrefetch());
		listenerContainer.setRecoveryInterval(this.consumerProperties.getRecoveryInterval());
		listenerContainer.setTxSize(this.consumerProperties.getTxSize());
		listenerContainer.setTaskExecutor(new SimpleAsyncTaskExecutor(this.getName() + "-"));
		listenerContainer.setQueueNames(queueName);
		listenerContainer.setAfterReceivePostProcessors(this.decompressingPostProcessor);
		listenerContainer.setMessagePropertiesConverter(INBOUN_MESSAGE_PROPERTIES_CONVERTER);
		listenerContainer.setExclusive(this.consumerProperties.isExclusive());
		listenerContainer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				System.out.println("Receiived Message: " + message);
				String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
				O result = consumerTarget.apply((I) messageBody);
				if (result != null){
					System.out.println("Processed via Finction: " + result);
					// TODO what do we do then (once we got the result)? I mean we need to publish it via producer
				}
			}
		});
		listenerContainer.afterPropertiesSet();
		listenerContainer.start();
	}

	@Override
	public void doStop() {
		System.out.println("####### STOPPING");
		if (logger.isInfoEnabled()) {
			logger.info("Stopping binding: " + this.getName());
		}
	}
}
