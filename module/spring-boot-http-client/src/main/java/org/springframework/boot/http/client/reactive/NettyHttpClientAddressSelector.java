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
 * See the License for the a specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.http.client.reactive;

import java.net.SocketAddress;
import java.util.List;

import reactor.netty.transport.ClientTransport;
import reactor.netty.transport.ClientTransportConfig;

import org.springframework.security.web.util.matcher.InetAddressMatcher;
import org.springframework.util.Assert;

/**
 * A {@link ClientTransport.ResolvedAddressSelector} that filters resolved addresses using
 * a {@link SecurityDnsHandler}.
 *
 * @author Phillip Webb
 * @author Kian Jamali
 */
class NettyHttpClientAddressSelector implements ClientTransport.ResolvedAddressSelector<ClientTransportConfig<?>> {

	private final InetAddressMatcher inetAddressMatcher;

	NettyHttpClientAddressSelector(InetAddressMatcher inetAddressMatcher) {
		Assert.notNull(inetAddressMatcher, "InetAddressMatcher must not be null");
		this.inetAddressMatcher = inetAddressMatcher;
	}

	@Override
	public List<? extends SocketAddress> apply(ClientTransportConfig<?> clientTransportConfig,
			List<? extends SocketAddress> resolvedAddresses) {
		if (resolvedAddresses == null || resolvedAddresses.isEmpty()) {
			return resolvedAddresses;
		}
		List<java.net.InetAddress> filteredIn = resolvedAddresses.stream()
				.filter(java.net.InetSocketAddress.class::isInstance)
				.map(java.net.InetSocketAddress.class::cast)
				.map(java.net.InetSocketAddress::getAddress)
				.filter(this.inetAddressMatcher::matches)
				.toList();

		return resolvedAddresses.stream()
				.filter(sa -> !(sa instanceof java.net.InetSocketAddress isa) || filteredIn.contains(isa.getAddress()))
				.collect(java.util.stream.Collectors.toList());
	}

}