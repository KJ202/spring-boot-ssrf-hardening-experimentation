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

import java.net.URI;
import java.net.UnknownHostException;

import org.apache.hc.client5.http.DnsResolver;
import org.eclipse.jetty.util.SocketAddressResolver;
import org.junit.jupiter.api.Test;

import org.springframework.boot.web.client.HttpComponentsDnsResolver;
import org.springframework.boot.web.client.JettyDnsResolver;
import org.springframework.security.web.util.matcher.InetAddressMatcher;
import org.springframework.security.web.util.matcher.InetAddressMatchers;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Usage example for SSRF hardening with Apache HttpComponents, Jetty, and Reactor Netty.
 */
class SsrfHardeningUsageExampleTests {

	@Test
	void apacheHttpClientWithSsrfHardening() {
		// 1. Create an InetAddressMatcher to identify external IPs.
		InetAddressMatcher externalMatcher = InetAddressMatchers.matchExternal().build();

		// 2. Create a DnsResolver that uses the InetAddressMatcher.
		// This adapter bridges the Spring Security matcher to the Apache HttpClient API.
		DnsResolver dnsResolver = new HttpComponentsDnsResolver(externalMatcher);

		// 3. Configure HttpClientSettings with the custom DnsResolver.
		HttpClientSettings settings = HttpClientSettings.defaults().withDnsResolver(dnsResolver);

		// 4. Build the ClientHttpRequestFactory using the settings.
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.httpComponents().build(settings);

		// 5. Use the factory to create requests.
		// Attempts to access blocked IPs will fail during DNS resolution.
		assertThatExceptionOfType(UnknownHostException.class).isThrownBy(() -> {
			requestFactory.createRequest(new URI("http://127.0.0.1"), HttpMethod.GET).execute();
		});
	}

	@Test
	void jettyHttpClientWithSsrfHardening() {
		// 1. Create an InetAddressMatcher to allow external IPs.
		InetAddressMatcher externalMatcher = InetAddressMatchers.matchExternal().build();

		// 2. Create a SocketAddressResolver that uses the InetAddressMatcher.
		SocketAddressResolver dnsResolver = new JettyDnsResolver(externalMatcher);

		// 3. Configure HttpClientSettings with the custom DnsResolver.
		HttpClientSettings settings = HttpClientSettings.defaults().withDnsResolver(dnsResolver);

		// 4. Build the ClientHttpRequestFactory using the settings.
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.jetty().build(settings);

		// 5. Use the factory to create requests.
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
			requestFactory.createRequest(new URI("http://127.0.0.1"), HttpMethod.GET).execute();
		});
	}

	@Test
	void reactorNettyHttpClientWithSsrfHardening() {
		// 1. Create an InetAddressMatcher to allow external IPs.
		InetAddressMatcher externalMatcher = InetAddressMatchers.matchExternal().build();

		// 2. Build the ClientHttpRequestFactory using a customizer to set the
		// dnsResolver.
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.reactor()
			.build(HttpClientSettings.defaults().withDnsResolver(externalMatcher));

		// 3. Use the factory to create requests.
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
			requestFactory.createRequest(new URI("http://127.0.0.1"), HttpMethod.GET).execute();
		});
	}

}
