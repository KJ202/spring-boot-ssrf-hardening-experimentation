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

import java.net.InetAddress;
import java.util.List;

import org.apache.hc.client5.http.DnsResolver;
import org.springframework.boot.http.client.dns.SecurityDnsHandler;

/**
 * {@link DnsResolver} implementation for Apache HttpComponents that provides SSRF protection
 * by validating DNS lookups against allowed patterns and IP ranges.
 *
 * @author KJ202
 * @since 3.4.0
 */
public class HttpComponentsDnsResolver implements DnsResolver {

    private final SecurityDnsHandler securityDnsHandler;

    private final List<String> allowedHostPatterns;

    private final List<String> allowedIpRanges;

    /**
     * Create a new {@link HttpComponentsDnsResolver} instance.
     * @param allowedHostPatterns the allowed host patterns
     * @param allowedIpRanges the allowed IP ranges
     */
    public HttpComponentsDnsResolver(List<String> allowedHostPatterns, List<String> allowedIpRanges) {
        this.allowedHostPatterns = allowedHostPatterns;
        this.allowedIpRanges = allowedIpRanges;
        this.securityDnsHandler = SecurityDnsHandler.builder()
                .allowList(allowedIpRanges.toArray(new String[0]))
                .blockAllExternal(true)
                .build();
    }

    @Override
    public InetAddress[] resolve(String host) {
        if (!isHostAllowed(host)) {
            throw new IllegalArgumentException("Host '" + host + "' is not allowed");
        }
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            List<InetAddress> filtered = this.securityDnsHandler.handleAddresses(List.of(addresses));
            if (filtered.isEmpty()) {
                throw new IllegalArgumentException("All resolved addresses for host '" + host + "' were filtered");
            }
            return filtered.toArray(new InetAddress[0]);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Failed to resolve host '" + host + "'", ex);
        }
    }

    @Override
    public String resolveCanonicalHostname(String host) {
        if (!isHostAllowed(host)) {
            throw new IllegalArgumentException("Host '" + host + "' is not allowed");
        }
        try {
            return InetAddress.getByName(host).getCanonicalHostName();
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Failed to resolve canonical hostname for '" + host + "'", ex);
        }
    }

    private boolean isHostAllowed(String host) {
        return this.allowedHostPatterns.stream().anyMatch(host::matches);
    }

    List<String> getAllowedHostPatterns() {
        return this.allowedHostPatterns;
    }

    List<String> getAllowedIpRanges() {
        return this.allowedIpRanges;
    }

}
