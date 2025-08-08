/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.http.client.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;

/**
 * {@link ConfigurationProperties @ConfigurationProperties} for a Spring's blocking HTTP
 * clients.
 *
 * @author Phillip Webb
 * @since 3.4.0
 * @see ClientHttpRequestFactorySettings
 */
@ConfigurationProperties("spring.http.client")
public class HttpClientProperties extends AbstractHttpRequestFactoryProperties {

	private final Dns dns = new Dns();

	public Dns getDns() {
		return this.dns;
	}

	/**
	 * DNS configuration.
	 */
	public static class Dns {

		/**
		 * Whether to enable DNS security.
		 */
		private boolean enabled = false;

		/**
		 * Allowed hosts.
		 */
		private List<String> allowedHosts = new ArrayList<>();

		/**
		 * Allowed IP ranges.
		 */
		private List<String> allowedIpRanges = new ArrayList<>();

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public List<String> getAllowedHosts() {
			return this.allowedHosts;
		}

		public void setAllowedHosts(List<String> allowedHosts) {
			this.allowedHosts = allowedHosts;
		}

		public List<String> getAllowedIpRanges() {
			return this.allowedIpRanges;
		}

		public void setAllowedIpRanges(List<String> allowedIpRanges) {
			this.allowedIpRanges = allowedIpRanges;
		}

	}

}