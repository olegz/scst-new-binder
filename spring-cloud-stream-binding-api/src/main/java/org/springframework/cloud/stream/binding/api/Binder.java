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
package org.springframework.cloud.stream.binding.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Binder<C,P> {

	//Binding<T>
	<I,O> void bindConsumer(String name, String group, Function<I,O> inboundBindTarget, C consumerProperties);

	default <I> void bindConsumer(String name, String group, Consumer<I> inboundBindTarget, C consumerProperties) {
		Function<I, Void> f = x -> {
			inboundBindTarget.accept(x);
			return null;
		};
		bindConsumer(name, group, f, consumerProperties);
	}

	/**
	 * Bind the target component as a message producer to the logical entity identified by
	 * the name.
	 * @param name the logical identity of the message target
	 * @param outboundBindTarget the app interface to be bound as a producer
	 * @param producerProperties the producer properties
	 */
	<O> void bindProducer(String name, Supplier<O> outboundBindTarget, P producerProperties);
}
