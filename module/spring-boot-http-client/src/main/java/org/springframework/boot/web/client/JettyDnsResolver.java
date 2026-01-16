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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;

/**
 * {@link SocketAddressResolver} implementation that delegates to a {@link SocketAddressResolver} and filters
 * the results using a {@link SecurityDnsHandler}.
 *
 * @author Kian Jamali
 * @since 3.5.0
 */
public class JettyDnsResolver implements SocketAddressResolver {

	private final SocketAddressResolver delegate;

	private final SecurityDnsHandler securityDnsHandler;

	public JettyDnsResolver(SecurityDnsHandler securityDnsHandler) {
		this(new SocketAddressResolver.Sync(), securityDnsHandler);
	}

	public JettyDnsResolver(SocketAddressResolver delegate, SecurityDnsHandler securityDnsHandler) {
		this.delegate = delegate;
		this.securityDnsHandler = securityDnsHandler;
	}

	@Override
	public void resolve(String host, int port, Map<String, Object> tag, Promise<List<InetSocketAddress>> promise) {
		this.delegate.resolve(host, port, tag, new Promise<>() {
			@Override
			public void succeeded(List<InetSocketAddress> result) {
				resolveSucceeded(host, result, promise);
			}

			@Override
			public void failed(Throwable x) {
				promise.failed(x);
			}
		});
	}

	private void resolveSucceeded(String host, List<InetSocketAddress> result, Promise<List<InetSocketAddress>> promise) {
		try {
			List<InetAddress> inetAddresses = new ArrayList<>();
			for (InetSocketAddress address : result) {
				inetAddresses.add(address.getAddress());
			}
			List<InetAddress> allowed = JettyDnsResolver.this.securityDnsHandler.handleAddresses(inetAddresses);
			if (allowed.isEmpty()) {
				promise.failed(new UnknownHostException("No allowed IP addresses found for " + host));
				return;
			}
			List<InetSocketAddress> allowedSocketAddresses = new ArrayList<>();
			for (InetSocketAddress address : result) {
				if (allowed.contains(address.getAddress())) {
					allowedSocketAddresses.add(address);
				}
			}
			promise.succeeded(allowedSocketAddresses);
		}
		catch (Exception ex) {
			promise.failed(ex);
		}
	}

}
