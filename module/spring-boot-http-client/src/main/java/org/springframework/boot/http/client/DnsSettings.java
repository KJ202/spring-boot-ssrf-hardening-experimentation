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
 * DNS settings that can be used to configure HTTP clients.
 *
 * @author KJ202
 * @since 3.4.0
 */
@ConfigurationProperties(prefix = "spring.http.client.dns")
public class DnsSettings {

    /**
     * Whether DNS security features are enabled.
     */
    private boolean enabled = false;

    /**
     * List of allowed host patterns.
     */
    private List<String> allowedHosts = new ArrayList<>();

    /**
     * List of allowed IP ranges in CIDR notation.
     */
    private List<String> allowedIpRanges = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getAllowedHosts() {
        return allowedHosts;
    }

    public void setAllowedHosts(List<String> allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    public List<String> getAllowedIpRanges() {
        return allowedIpRanges;
    }

    public void setAllowedIpRanges(List<String> allowedIpRanges) {
        this.allowedIpRanges = allowedIpRanges;
    }
}
