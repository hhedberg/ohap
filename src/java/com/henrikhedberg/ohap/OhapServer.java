
/*
 * Open Home Automation Protocol (OHAP) Reference Server Implementation
 * Copyright (C) 2015-2016 Henrik Hedberg <henrik.hedberg@iki.fi>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.henrikhedberg.ohap;

import com.henrikhedberg.hbdp.server.*;
import java.util.HashMap;
import java.io.IOException;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;

/**
 * Open Home Automation Protocol (OHAP) server.
 *
 * <p>The implementation relies on the {@link HbdpServer} and
 * the <code>com.sun.new.httpserver</code> package.
 *
 * @author Henrik Hedberg &lt;henrik.hedberg@iki.fi&gt;
 * @version 1.1 (20160311)
 */
public class OhapServer implements HbdpConnection.Handler {

	public static final int MESSAGE_TYPE_LOGIN = 0;
	public static final int MESSAGE_TYPE_LOGOUT = 1;
	public static final int MESSAGE_TYPE_PING = 2;
	public static final int MESSAGE_TYPE_PONG = 3;
	public static final int MESSAGE_TYPE_DECIMAL_SENSOR = 4;
	public static final int MESSAGE_TYPE_DECIMAL_ACTUATOR = 5;
	public static final int MESSAGE_TYPE_BINARY_SENSOR= 6;
	public static final int MESSAGE_TYPE_BINARY_ACTUATOR = 7;
	public static final int MESSAGE_TYPE_CONTAINER = 8;
	public static final int MESSAGE_TYPE_DECIMAL_CHANGED = 9;
	public static final int MESSAGE_TYPE_BINARY_CHANGED = 10;
	public static final int MESSAGE_TYPE_ITEM_REMOVED = 11;
	public static final int MESSAGE_TYPE_LISTENING_START = 12;
	public static final int MESSAGE_TYPE_LISTENING_STOP = 13;

	private HashMap<Long, Item> items = new HashMap<>();

	public OhapServer() {
		Container rootContainer = new Container(0, "OHAP Test Server", "This site provides a test server that can be used when testing clients implementing the open home automation protocol. The usage of the server is not allowed for any other purpose, nor it must be tried to operate against the specification. ", false);
		addItem(rootContainer);
		rootContainer.attachToServer(this);

		BinaryDevice mains = new BinaryDevice(2, "Mains Switch", "The main mains power switch", false, Device.Type.ACTUATOR, true);
		rootContainer.addItem(mains);
		
		Container room1 = new Container(1, "Room 1", "One room.", false);
		rootContainer.addItem(room1);

		DecimalDevice temperature = new DecimalDevice(3, "Temperature", "The temperature of the Room 1", false, Device.Type.SENSOR, 19.2, -50, +50, "C", "Celsius Degrees");
		room1.addItem(temperature);
		
	}

	public Item getItemByIdentifier(long identifier) {
		return items.get(Long.valueOf(identifier));
	}
	
	public void addItem(Item item) {
		items.put(Long.valueOf(item.getIdentifier()), item);
	}
	
	public boolean authenticateUser(String name, String password) {
		return true;
	}

	public void handle(HbdpConnection connection) {
		OhapSession session = new OhapSession(this, connection);
	}
	
	public static void main(String[] args) throws IOException {
		OhapServer ohapServer = new OhapServer();
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(18000), 10);
		HttpContext httpContext = httpServer.createContext("/");
		HbdpServer hbdpServer = new HbdpServer(httpContext, ohapServer);
		httpServer.setExecutor(null);
		httpServer.start();
	}
}
