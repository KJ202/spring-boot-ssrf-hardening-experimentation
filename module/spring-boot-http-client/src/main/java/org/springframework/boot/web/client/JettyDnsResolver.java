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
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;

import org.springframework.security.web.util.matcher.InetAddressMatcher;
import org.springframework.util.Assert;

/**
 * {@link SocketAddressResolver} implementation that delegates to a
 * {@link SocketAddressResolver} and filters the results using a
 * {@link SecurityDnsHandler}.
 *
 * @author Kian Jamali
 * @since 3.5.0
 */
public class JettyDnsResolver implements SocketAddressResolver {

	private final SocketAddressResolver delegate;

	private final InetAddressMatcher inetAddressMatcher;

	public JettyDnsResolver(InetAddressMatcher inetAddressMatcher) {
		this(new SocketAddressResolver.Sync(), inetAddressMatcher);
	}

	public JettyDnsResolver(SocketAddressResolver delegate, InetAddressMatcher inetAddressMatcher) {
		Assert.notNull(delegate, "Delegate must not be null");
		Assert.notNull(inetAddressMatcher, "InetAddressMatcher must not be null");
		this.delegate = delegate;
		this.inetAddressMatcher = inetAddressMatcher;
	}

	@Override
	public void resolve(String host, int port, Map<String, Object> tag, Promise<List<InetSocketAddress>> promise) {
		this.delegate.resolve(host, port, tag, new Promise<>() {

			@Override
			public void succeeded(List<InetSocketAddress> result) {
				promise.succeeded(result.stream()
						.map(InetSocketAddress::getAddress)
						.filter(inetAddressMatcher::matches)
						.map(address -> new InetSocketAddress(address, port))
						.toList());
			}

			@Override
			public void failed(Throwable x) {
				promise.failed(x);
			}
		});
	}

}
