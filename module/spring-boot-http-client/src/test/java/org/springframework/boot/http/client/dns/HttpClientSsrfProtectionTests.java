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

package org.springframework.boot.http.client.dns;

import org.apache.hc.client5.http.DnsResolver;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.client.HttpComponentsDnsResolver;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for SSRF protection in HTTP client with {@link HttpComponentsDnsResolver}.
 *
 * @author KJ202
 */
class HttpClientSsrfProtectionTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void whenDnsEnabledAndHostAllowedThenResolvesToValidAddress() {
        this.contextRunner.withPropertyValues(
                "spring.http.client.dns.enabled=true",
                "spring.http.client.dns.allowed-hosts[0]=^api\\.example\\.com$",
                "spring.http.client.dns.allowed-ip-ranges[0]=203.0.113.0/24"
        ).run((context) -> {
            HttpComponentsDnsResolver resolver = context.getBean(HttpComponentsDnsResolver.class);
            InetAddress[] addresses = resolver.resolve("api.example.com");
            assertThat(addresses).isNotEmpty();
        });
    }

    @Test
    void whenDnsEnabledAndHostNotAllowedThenThrowsException() {
        this.contextRunner.withPropertyValues(
                "spring.http.client.dns.enabled=true",
                "spring.http.client.dns.allowed-hosts[0]=^api\\.example\\.com$"
        ).run((context) -> {
            HttpComponentsDnsResolver resolver = context.getBean(HttpComponentsDnsResolver.class);
            assertThatThrownBy(() -> resolver.resolve("evil.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Host 'evil.com' is not allowed");
        });
    }

    @Test
    void whenDnsEnabledAndIpNotAllowedThenThrowsException() {
        this.contextRunner.withPropertyValues(
                "spring.http.client.dns.enabled=true",
                "spring.http.client.dns.allowed-hosts[0]=^api\\.example\\.com$",
                "spring.http.client.dns.allowed-ip-ranges[0]=203.0.113.0/24"
        ).run((context) -> {
            HttpComponentsDnsResolver resolver = context.getBean(HttpComponentsDnsResolver.class);
            assertThatThrownBy(() -> resolver.resolve("internal-service"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Host 'internal-service' is not allowed");
        });
    }

    

}