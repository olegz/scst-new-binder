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

import org.springframework.cloud.stream.binding.api.Binding;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
public abstract class AbstractBinding implements Binding {

	private boolean running;

	private final String bindingName;

	public AbstractBinding(String targetBeanName) {
		this.bindingName = BindingUtils.generateBindingName(targetBeanName);
	}

	@Override
	public String getName() {
		return this.bindingName;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		callback.run();
		this.stop();
	}

	@Override
	public void start() {
		this.doStart();
		this.running = true;
	}

	public abstract void doStart();

	@Override
	public void stop() {
		this.doStop();
		this.running = false;
	}

	public abstract void doStop();

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public int getPhase() {
		return 0;
	}
}
