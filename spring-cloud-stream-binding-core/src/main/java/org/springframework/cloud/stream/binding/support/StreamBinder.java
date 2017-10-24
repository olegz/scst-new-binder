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
package org.springframework.cloud.stream.binding.support;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.cloud.stream.binding.api.Binding;
import org.springframework.cloud.stream.binding.api.BindingFactory;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
@Configuration
class StreamBinder<P extends ProducerProperties, C extends ConsumerProperties> implements InitializingBean, BeanFactoryAware {

	private final Logger logger = LoggerFactory.getLogger(StreamBinder.class);

	/*
	 * Holds type mappings for all functional beans (Supplier,Function,Consumer) gathered by this binder
	 */
	private Map<String, Type[]> functionsTypeMappings;

	private DefaultListableBeanFactory beanFactory;

	@Autowired(required=false)
	protected Map<String, Function<?,?>> functions;

	@Autowired(required=false)
	private Map<String, Consumer<?>> consumers;

	@Autowired(required=false)
	private Map<String, Supplier<?>> suppliers;

	@Autowired
	private BindingFactory<P,C> bindingFactory;

	@Autowired
	private P producerProperties;

	@Autowired
	private C consumerProperties;

	@Override
	public void afterPropertiesSet() throws Exception {
		/*
		 * Binding as SmartLifectcle should be registered with the BF
		 * Also, ListenerContainer code and all the lifecycle stuff should be done only at the binding level,
		 * Would be nice if in the end Binding is all that needs to be implemented by the user.
		 */
		this.functionsTypeMappings = new HashMap<>();
		if (this.functions != null) {
			for (String beanName : this.functions.keySet()) {
				this.addTypeMappings(beanName);
				//TODO doesn't group comes from properties? If so why are we passing it here?
				Binding binding = this.bindingFactory.bindConsumer(beanName, "", this.functions.get(beanName), this.consumerProperties);
				this.beanFactory.registerSingleton(binding.getName(), binding);
			}
		}
		if (this.consumers != null) {
			for (String beanName : this.consumers.keySet()) {
				this.addTypeMappings(beanName);
				Binding binding = this.bindingFactory.bindConsumer(beanName, "", this.consumers.get(beanName), this.consumerProperties);
				this.beanFactory.registerSingleton(binding.getName(), binding);
			}
		}
		if (this.suppliers != null) {
			for (String beanName : this.suppliers.keySet()) {
				this.addTypeMappings(beanName);
				Binding binding = this.bindingFactory.bindProducer(beanName, this.suppliers.get(beanName), this.producerProperties);
				this.beanFactory.registerSingleton(binding.getName(), binding);
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
		Class<?> functionalInterface = method.getReturnType().isAssignableFrom(Function.class)
				? Function.class : (method.getReturnType().isAssignableFrom(Consumer.class) ? Consumer.class : Supplier.class);
		Type[] types = retrieveTypes(method.getGenericReturnType(), functionalInterface);
		if (logger.isDebugEnabled()){
			logger.debug("Added type mappings: " + beanName + "(" + Arrays.asList(types).toString().replaceAll("\\[", "").replaceAll("]", "") + ")");
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
