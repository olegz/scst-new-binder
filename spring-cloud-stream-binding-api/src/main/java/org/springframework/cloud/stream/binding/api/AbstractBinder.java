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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class AbstractBinder implements Binder<Properties, Properties>, InitializingBean, BeanFactoryAware {

	private final Logger logger = LoggerFactory.getLogger(AbstractBinder.class);

	/*
	 * Hold type mappings for all functional beans (Supplier,Function,Consumer) gathered by this binder
	 */
	private Map<String, Type[]> functionsTypeMappings;

	private DefaultListableBeanFactory beanFactory;

	@Autowired(required=false)
	protected Map<String, Function<?,?>> functions;

	@Autowired(required=false)
	private Map<String, Consumer<?>> consumers;

	@Autowired(required=false)
	private Map<String, Supplier<?>> suppliers;


	@Override
	public <I, O> void bindConsumer(String name, String group,
			Function<I, O> inboundBindTarget, Properties consumerProperties) {

	}

	@Override
	public <O> void bindProducer(String name, Supplier<O> outboundBindTarget,
			Properties producerProperties) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.functionsTypeMappings = new HashMap<>();
		if (this.functions != null) {
			for (String beanName : this.functions.keySet()) {
				this.addTypeMappings(beanName);
			}
		}
		if (this.consumers != null) {
			for (String beanName : this.consumers.keySet()) {
				this.addTypeMappings(beanName);
			}
		}
		if (this.suppliers != null) {
			for (String beanName : this.suppliers.keySet()) {
				this.addTypeMappings(beanName);
			}
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory) beanFactory;
	}

	private void addTypeMappings(String beanName){
		RootBeanDefinition beanDefinition = (RootBeanDefinition) beanFactory.getMergedBeanDefinition(beanName);
		Method method = beanDefinition.getResolvedFactoryMethod();
		Type[] types = retrieveTypes(method.getGenericReturnType(), Function.class);
		if (logger.isDebugEnabled()){
			logger.debug("Added type mappings: {" + beanName + ":" + Arrays.asList(types) + "}");
		}
		this.functionsTypeMappings.put(beanName, types);
	}

	private Type[] retrieveTypes(Type genericInterface, Class<?> interfaceClass){
		if ((genericInterface instanceof ParameterizedType) && interfaceClass
				.getTypeName().equals(((ParameterizedType) genericInterface).getRawType().getTypeName())) {
			ParameterizedType type = (ParameterizedType) genericInterface;
			Type[] args = type.getActualTypeArguments();
			return args;
		}
		return null;
	}
}
