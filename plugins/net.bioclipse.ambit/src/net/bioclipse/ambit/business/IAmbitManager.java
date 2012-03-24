/*******************************************************************************
 * Copyright (c) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ambit.business;

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Manager exposing AMBIT functonality.",
    doi="10.1186/1758-2946-3-18"
)
public interface IAmbitManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        methodSummary = "Calculates the pKa value for the molecule.",
        params="IMolecule molecule"
    )
    public double calculatePKa(IMolecule molecule)
        throws BioclipseException;

    @PublishedMethod ( 
        params = "IMolecule molecule, String smarts",
        methodSummary = "Returns true if the given SMARTS matches the given " +
    		"molecule" )
    @Recorded
    public boolean smartsMatches(IMolecule molecule, String smarts )
    	throws BioclipseException;

    @Recorded
    @PublishedMethod(
        methodSummary = "Counts the OECD functional group counts.",
        params="IMolecule molecule"
    )
    public List<Integer> countOECDGroups(IMolecule molecule)
        throws BioclipseException;
}
