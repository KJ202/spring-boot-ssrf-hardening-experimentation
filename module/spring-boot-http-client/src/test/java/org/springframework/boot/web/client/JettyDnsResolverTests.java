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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.security.web.util.matcher.InetAddressMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link JettyDnsResolver}.
 *
 * @author Kian Jamali
 */
@SuppressWarnings({ "unchecked", "NullAway" })
class JettyDnsResolverTests {

	private final SocketAddressResolver delegate = mock(SocketAddressResolver.class);

	private final InetAddressMatcher matcher = mock(InetAddressMatcher.class);

	private final JettyDnsResolver resolver = new JettyDnsResolver(this.delegate, this.matcher);

	@Test
	void createWhenDelegateIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new JettyDnsResolver(null, this.matcher))
			.withMessage("Delegate must not be null");
	}

	@Test
	void createWhenMatcherIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new JettyDnsResolver(this.delegate, null))
			.withMessage("InetAddressMatcher must not be null");
	}

	@Test
	void resolveWhenMatcherAllowsAddressShouldSucceed() throws Exception {
		Promise<List<InetSocketAddress>> clientPromise = mock(Promise.class);
		Map<String, Object> map = mock(Map.class);

		this.resolver.resolve("example.com", 8080, map, clientPromise);

		ArgumentCaptor<Promise<List<InetSocketAddress>>> wrappedPromiseCaptor = ArgumentCaptor.forClass(Promise.class);
		verify(this.delegate).resolve(eq("example.com"), eq(8080), eq(map), wrappedPromiseCaptor.capture());

		Promise<List<InetSocketAddress>> wrappedPromise = wrappedPromiseCaptor.getValue();
		InetAddress address = InetAddress.getByName("192.168.1.1");
		InetSocketAddress socketAddress = new InetSocketAddress(address, 8080);

		given(this.matcher.matches(address)).willReturn(true);
		wrappedPromise.succeeded(List.of(socketAddress));

		verify(clientPromise).succeeded(List.of(socketAddress));
	}

	@Test
	void resolveWhenMatcherBlocksAddressShouldFail() throws Exception {
		Promise<List<InetSocketAddress>> clientPromise = mock(Promise.class);
		Map<String, Object> map = mock(Map.class);

		this.resolver.resolve("example.com", 8080, map, clientPromise);

		ArgumentCaptor<Promise<List<InetSocketAddress>>> wrappedPromiseCaptor = ArgumentCaptor.forClass(Promise.class);
		verify(this.delegate).resolve(eq("example.com"), eq(8080), eq(map), wrappedPromiseCaptor.capture());

		Promise<List<InetSocketAddress>> wrappedPromise = wrappedPromiseCaptor.getValue();
		InetAddress address = InetAddress.getByName("192.168.1.1");
		InetSocketAddress socketAddress = new InetSocketAddress(address, 8080);

		given(this.matcher.matches(address)).willReturn(false);
		wrappedPromise.succeeded(List.of(socketAddress));

		ArgumentCaptor<Throwable> exceptionCaptor = ArgumentCaptor.forClass(Throwable.class);
		verify(clientPromise).failed(exceptionCaptor.capture());
		assertThat(exceptionCaptor.getValue()).isInstanceOf(UnknownHostException.class)
			.hasMessage("No allowed IP addresses found for example.com");
	}

	@Test
	void resolveWhenDelegateFailsShouldFail() {
		Promise<List<InetSocketAddress>> clientPromise = mock(Promise.class);
		Map<String, Object> map = mock(Map.class);

		this.resolver.resolve("example.com", 8080, map, clientPromise);

		ArgumentCaptor<Promise<List<InetSocketAddress>>> wrappedPromiseCaptor = ArgumentCaptor.forClass(Promise.class);
		verify(this.delegate).resolve(eq("example.com"), eq(8080), eq(map), wrappedPromiseCaptor.capture());

		Promise<List<InetSocketAddress>> wrappedPromise = wrappedPromiseCaptor.getValue();

		RuntimeException failure = new RuntimeException("test error");
		wrappedPromise.failed(failure);

		verify(clientPromise).failed(failure);
	}

}
