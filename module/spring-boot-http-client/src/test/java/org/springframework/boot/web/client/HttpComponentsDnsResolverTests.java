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

package org.springframework.boot.web.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.hc.client5.http.DnsResolver;
import org.junit.jupiter.api.Test;

import org.springframework.security.web.util.matcher.InetAddressMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link HttpComponentsDnsResolver}.
 *
 * @author Scott Frederick
 * @author Kian Jamali
 */
@SuppressWarnings("NullAway")
class HttpComponentsDnsResolverTests {

	private final DnsResolver delegate = mock(DnsResolver.class);

	private final InetAddressMatcher matcher = mock(InetAddressMatcher.class);

	private final HttpComponentsDnsResolver resolver = new HttpComponentsDnsResolver(this.delegate, this.matcher);

	@Test
	void createWhenDelegateIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new HttpComponentsDnsResolver(null, this.matcher))
			.withMessage("Delegate must not be null");
	}

	@Test
	void createWhenMatcherIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new HttpComponentsDnsResolver(this.delegate, null))
			.withMessage("InetAddressMatcher must not be null");
	}

	@Test
	void resolveWhenMatcherAllowsAddressShouldReturnAddress() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		given(this.delegate.resolve("example.com")).willReturn(new InetAddress[] { address });
		given(this.matcher.matches(address)).willReturn(true);
		InetAddress[] resolved = this.resolver.resolve("example.com");
		assertThat(resolved).containsExactly(address);
	}

	@Test
	void resolveWhenMatcherBlocksAddressShouldThrowException() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		given(this.delegate.resolve("example.com")).willReturn(new InetAddress[] { address });
		given(this.matcher.matches(address)).willReturn(false);
		assertThatExceptionOfType(UnknownHostException.class).isThrownBy(() -> this.resolver.resolve("example.com"))
			.withMessage("No allowed IP addresses found for example.com");
	}

	@Test
	void resolveWhenMatcherAllowsSomeAddressesShouldReturnAllowed() throws Exception {
		InetAddress address1 = InetAddress.getByName("192.168.1.1");
		InetAddress address2 = InetAddress.getByName("10.0.0.1");
		given(this.delegate.resolve("example.com")).willReturn(new InetAddress[] { address1, address2 });
		given(this.matcher.matches(address1)).willReturn(true);
		given(this.matcher.matches(address2)).willReturn(false);
		InetAddress[] resolved = this.resolver.resolve("example.com");
		assertThat(resolved).containsExactly(address1);
	}

	@Test
	void resolveCanonicalHostnameShouldDelegate() throws Exception {
		given(this.delegate.resolveCanonicalHostname("example.com")).willReturn("canonical.example.com");
		String host = this.resolver.resolveCanonicalHostname("example.com");
		assertThat(host).isEqualTo("canonical.example.com");
		verify(this.delegate).resolveCanonicalHostname("example.com");
	}

}
