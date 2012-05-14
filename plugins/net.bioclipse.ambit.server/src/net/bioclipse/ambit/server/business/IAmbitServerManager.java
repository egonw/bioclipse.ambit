/*******************************************************************************
 * Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ambit.server.business;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Manager to control a local AMBIT2 server."
)
public interface IAmbitServerManager extends IBioclipseManager {
	
	@PublishedMethod(
		methodSummary = "Boots a local AMBIT2 server, using the given MySQL credentials to the database backend" +
			"at the given port.",
		params = "String username, String password, int port" 
	)
	public void boot(String username, String password, int port) throws BioclipseException;

	@PublishedMethod(
		methodSummary = "Shuts down the local AMBIT2 server"
	)
	public void shutdown() throws BioclipseException;

}
