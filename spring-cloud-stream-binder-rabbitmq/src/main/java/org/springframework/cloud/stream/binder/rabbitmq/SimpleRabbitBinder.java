/*
x * Copyright 2017 the original author or authors.
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

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.api.Binder;
import org.springframework.context.annotation.Configuration;

@Configuration
class SimpleRabbitBinder<I,O> implements Binder, InitializingBean {

	@Autowired(required=false)
	private List<Function<I,O>> functions;

	@Autowired(required=false)
	private List<Consumer<I>> consumers;

	@Autowired(required=false)
	private List<Supplier<O>> suppliers;

	public SimpleRabbitBinder(){
		System.out.println("########## Hello from Binder ^^^^^^^^ ");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("Functions: " + functions);
		System.out.println("Consumers: " + consumers);
		System.out.println("Suppliers: " + suppliers);
	}
}
