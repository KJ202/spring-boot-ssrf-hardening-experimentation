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

package org.springframework.boot.http.client.reactive;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.junit.jupiter.api.Test;
import reactor.netty.transport.ClientTransportConfig;

import org.springframework.security.web.util.matcher.InetAddressMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link NettyHttpClientAddressSelector}.
 *
 * @author Phillip Webb
 * @author Kian Jamali
 */
@SuppressWarnings("NullAway")
class NettyHttpClientAddressSelectorTests {

	private final InetAddressMatcher matcher = mock(InetAddressMatcher.class);

	private final NettyHttpClientAddressSelector selector = new NettyHttpClientAddressSelector(this.matcher);

	private final ClientTransportConfig<?> config = mock(ClientTransportConfig.class);

	@Test
	void createWhenMatcherIsNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new NettyHttpClientAddressSelector(null))
			.withMessage("InetAddressMatcher must not be null");
	}

	@Test
	void applyWhenEmptyShouldReturnEmpty() {
		List<? extends SocketAddress> allowed = this.selector.apply(this.config, List.of());
		assertThat(allowed).isEmpty();
	}

	@Test
	void applyWhenMatcherAllowsAddressShouldReturnAddress() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		InetSocketAddress socketAddress = new InetSocketAddress(address, 8080);
		given(this.matcher.matches(address)).willReturn(true);
		@SuppressWarnings("unchecked")
		List<SocketAddress> allowed = (List<SocketAddress>) this.selector.apply(this.config, List.of(socketAddress));
		assertThat(allowed).containsExactly(socketAddress);
	}

	@Test
	void applyWhenMatcherBlocksAddressShouldThrowException() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.1");
		InetSocketAddress socketAddress = new InetSocketAddress(address, 8080);
		given(this.matcher.matches(address)).willReturn(false);
		assertThatIllegalArgumentException().isThrownBy(() -> this.selector.apply(this.config, List.of(socketAddress)))
			.withMessage("No allowed IP addresses found");
	}

	@Test
	void applyWhenNonInetSocketAddressShouldIgnoreMatcher() {
		SocketAddress customAddress = new SocketAddress() {
		};
		@SuppressWarnings("unchecked")
		List<SocketAddress> allowed = (List<SocketAddress>) this.selector.apply(this.config, List.of(customAddress));
		assertThat(allowed).containsExactly(customAddress);
	}

}
