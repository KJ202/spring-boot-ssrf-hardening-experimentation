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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;

/**
 * A {@link SocketAddressResolver} that uses a {@link BannedHostDnsResolver} to resolve
 * hosts and ban specific hosts.
 *
 * @author Moritz Halbritter
 */
class BannedHostSocketAddressResolver implements SocketAddressResolver {

	private final String bannedHost;

	private final SystemDefaultDnsResolver defaultDnsResolver = new SystemDefaultDnsResolver();

	BannedHostSocketAddressResolver(String bannedHost) {
		this.bannedHost = bannedHost;
	}

	@Override
	public void resolve(String host, int port, Promise<List<InetSocketAddress>> promise) {
		try {
			if (host.equals(this.bannedHost)) {
				throw new UnknownHostException(host);
			}
			InetAddress[] addresses = this.defaultDnsResolver.resolve(host);
			List<InetSocketAddress> socketAddresses = new ArrayList<>(addresses.length);
			for (InetAddress address : addresses) {
				socketAddresses.add(new InetSocketAddress(address, port));
			}
			promise.succeeded(socketAddresses);
		}
		catch (UnknownHostException ex) {
			promise.failed(ex);
		}
	}

}
