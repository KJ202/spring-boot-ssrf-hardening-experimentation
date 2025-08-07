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

package org.springframework.boot.http.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for HTTP client DNS security.
 *
 * @author KJ202
 * @since 3.4.0
 */
@ConfigurationProperties(prefix = "spring.http.client.dns-security")
public class DnsSecurityProperties {

	/**
	 * Whether to enable DNS security features.
	 */
	private boolean enabled = false;

	/**
	 * Whether to run in report-only mode. When true, violations are logged but not blocked.
	 */
	private boolean reportOnly = false;

	/**
	 * Whether to block access to all internal IP addresses.
	 */
	private boolean blockAllInternal = false;

	/**
	 * Whether to block access to all external IP addresses.
	 */
	private boolean blockAllExternal = false;

	/**
	 * List of IP addresses or CIDR ranges to explicitly block.
	 */
	private List<String> denyList = new ArrayList<>();

	/**
	 * List of IP addresses or CIDR ranges to explicitly allow.
	 */
	private List<String> allowList = new ArrayList<>();

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isReportOnly() {
		return this.reportOnly;
	}

	public void setReportOnly(boolean reportOnly) {
		this.reportOnly = reportOnly;
	}

	public boolean isBlockAllInternal() {
		return this.blockAllInternal;
	}

	public void setBlockAllInternal(boolean blockAllInternal) {
		this.blockAllInternal = blockAllInternal;
	}

	public boolean isBlockAllExternal() {
		return this.blockAllExternal;
	}

	public void setBlockAllExternal(boolean blockAllExternal) {
		this.blockAllExternal = blockAllExternal;
	}

	public List<String> getDenyList() {
		return this.denyList;
	}

	public void setDenyList(List<String> denyList) {
		this.denyList = denyList;
	}

	public List<String> getAllowList() {
		return this.allowList;
	}

	public void setAllowList(List<String> allowList) {
		this.allowList = allowList;
	}

}
