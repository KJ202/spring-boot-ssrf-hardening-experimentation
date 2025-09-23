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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import reactor.netty.transport.ClientTransport;
import reactor.netty.transport.ClientTransportConfig;

import org.springframework.util.Assert;

/**
 * A {@link ClientTransport.ResolvedAddressSelector} that filters resolved addresses based
 * on a set of banned hosts.
 *
 * @author Phillip Webb
 */
class NettyHttpClientAddressSelector implements ClientTransport.ResolvedAddressSelector<ClientTransportConfig<?>> {

	private final Predicate<InetSocketAddress> filter;

	NettyHttpClientAddressSelector(Set<String> bannedHosts) {
		Assert.notNull(bannedHosts, "BannedHosts must not be null");
		this.filter = (address) -> !bannedHosts.contains(address.getHostName());
	}

	private boolean isAllowed(SocketAddress address) {
		if (address instanceof InetSocketAddress inetSocketAddress) {
			return this.filter.test(inetSocketAddress);
		}
		return true;
	}

	@Override
	public List<? extends SocketAddress> apply(ClientTransportConfig<?> clientTransportConfig,
			List<? extends SocketAddress> resolvedAddresses) {
		if (resolvedAddresses.isEmpty()) {
			return resolvedAddresses;
		}
		return resolvedAddresses.stream().filter(this::isAllowed).toList();
	}

}
