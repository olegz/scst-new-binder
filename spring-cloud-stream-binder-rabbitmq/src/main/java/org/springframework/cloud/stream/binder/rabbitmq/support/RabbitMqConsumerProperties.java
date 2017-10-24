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

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binding.config.BinderEnvironmentConfigurationProperties;
import org.springframework.cloud.stream.binding.support.ConsumerProperties;

@ConfigurationProperties(BinderEnvironmentConfigurationProperties.PREFIX + ".consumer.rabbit")
public class RabbitMqConsumerProperties extends ConsumerProperties {

	@Autowired
	private RabbitMqCommonProperties commonProperties;

	private boolean transacted;

	private AcknowledgeMode acknowledgeMode = AcknowledgeMode.AUTO;

	private int maxConcurrency = 1;

	private int prefetch = 1;

	private int txSize = 1;

	private boolean durableSubscription = true;

	private boolean republishToDlq;

	private MessageDeliveryMode republishDeliveyMode = MessageDeliveryMode.PERSISTENT;

	private boolean requeueRejected = false;

	private String[] headerPatterns = new String[] {"*"};

	private long recoveryInterval = 5000;

	/**
	 * True if the consumer is exclusive.
	 */
	private boolean exclusive;

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

	public RabbitMqCommonProperties getCommonProperties() {
		return commonProperties;
	}

	public boolean isTransacted() {
		return transacted;
	}

	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

	public AcknowledgeMode getAcknowledgeMode() {
		return acknowledgeMode;
	}

	public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
		this.acknowledgeMode = acknowledgeMode;
	}

	public int getMaxConcurrency() {
		return maxConcurrency;
	}

	public void setMaxConcurrency(int maxConcurrency) {
		this.maxConcurrency = maxConcurrency;
	}

	public int getPrefetch() {
		return prefetch;
	}

	public void setPrefetch(int prefetch) {
		this.prefetch = prefetch;
	}

	public int getTxSize() {
		return txSize;
	}

	public void setTxSize(int txSize) {
		this.txSize = txSize;
	}

	public boolean isDurableSubscription() {
		return durableSubscription;
	}

	public void setDurableSubscription(boolean durableSubscription) {
		this.durableSubscription = durableSubscription;
	}

	public boolean isRepublishToDlq() {
		return republishToDlq;
	}

	public void setRepublishToDlq(boolean republishToDlq) {
		this.republishToDlq = republishToDlq;
	}

	public MessageDeliveryMode getRepublishDeliveyMode() {
		return republishDeliveyMode;
	}

	public void setRepublishDeliveyMode(MessageDeliveryMode republishDeliveyMode) {
		this.republishDeliveyMode = republishDeliveyMode;
	}

	public boolean isRequeueRejected() {
		return requeueRejected;
	}

	public void setRequeueRejected(boolean requeueRejected) {
		this.requeueRejected = requeueRejected;
	}

	public String[] getHeaderPatterns() {
		return headerPatterns;
	}

	public void setHeaderPatterns(String[] headerPatterns) {
		this.headerPatterns = headerPatterns;
	}

	public long getRecoveryInterval() {
		return recoveryInterval;
	}

	public void setRecoveryInterval(long recoveryInterval) {
		this.recoveryInterval = recoveryInterval;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
}
