/*******************************************************************************
 * Copyright (c) 2012  Egon Willighagen <egonw@users.sf.net>
 *               2008  Nikolay Kochev <nick@uni-plovdiv.bg>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.ambit.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.managers.business.IBioclipseManager;

import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;

import ambit2.smarts.SmartsParser;

public class AmbitManager implements IBioclipseManager {

    private static final Logger logger = Logger.getLogger(AmbitManager.class);
    private static final SmartsParser smartsParser = new SmartsParser();

    private CDKManager cdk = new CDKManager();

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "ambit";
    }

    public double calculatePKa(IMolecule molecule) throws BioclipseException {
    	logger.debug("Take a reasonable default");
    	ICDKMolecule cdkMol = cdk.asCDKMolecule(molecule);
    	
    	return 7.0;
    }

    public boolean smartsMatches(IMolecule molecule, String smarts )
    	throws BioclipseException {
    	ICDKMolecule cdkMol = cdk.asCDKMolecule(molecule);

    	QueryAtomContainer query = smartsParser.parse(smarts);
    	String error = smartsParser.getErrorMessages();
    	if (error.length() != 0) {
    		throw new BioclipseException("SMARTS Parser error: " + error);
    	}
    	IAtomContainer atomContainer = cdkMol.getAtomContainer();
    	smartsParser.setSMARTSData(atomContainer);

    	try {
			return matches(atomContainer, query);
		} catch (CDKException exception) {
			throw new BioclipseException("SMARTS matching error: " + error);
		}
    }

	private boolean matches(IAtomContainer atomContainer, QueryAtomContainer query) throws CDKException {
		List<List<Integer>> matchingAtoms;

		// lets see if we have a single atom query
		if (query.getAtomCount() == 1) {
			// lets get the query atom
			IQueryAtom queryAtom = (IQueryAtom) query.getAtom(0);			
			matchingAtoms = new ArrayList<List<Integer>>();
			Iterator<IAtom> atoms = atomContainer.atoms().iterator();
			while (atoms.hasNext()) 
			{
				IAtom atom = atoms.next();
				if (queryAtom.matches(atom)) {
					List<Integer> tmp = new ArrayList<Integer>();
					tmp.add(atomContainer.getAtomNumber(atom));
					matchingAtoms.add(tmp);
				}
			}
		} else {
			List<List<RMap>> bondMapping = UniversalIsomorphismTester.getSubgraphMaps(atomContainer, query);
			matchingAtoms = getAtomMappings(bondMapping, atomContainer);
		}		
		return matchingAtoms.size() != 0;
	}

	private List<List<Integer>> getAtomMappings(List<List<RMap>> bondMapping, IAtomContainer atomContainer) {
		List<List<Integer>> atomMapping = new ArrayList<List<Integer>>();
		// loop over each mapping
		for (List<RMap> aBondMapping : bondMapping) {
			List<RMap> list = aBondMapping;
			
			List<Integer> tmp = new ArrayList<Integer>();
			IAtom atom1 = null;
			IAtom atom2 = null;
			// loop over this mapping
			for (Object aList : list) {
				RMap map = (RMap) aList;
				int bondID = map.getId1();
				
				// get the atoms in this bond
				IBond bond = atomContainer.getBond(bondID);
				atom1 = bond.getAtom(0);
				atom2 = bond.getAtom(1);
				
				Integer idx1 = atomContainer.getAtomNumber(atom1);
				Integer idx2 = atomContainer.getAtomNumber(atom2);
				
				if (!tmp.contains(idx1)) tmp.add(idx1);
				if (!tmp.contains(idx2)) tmp.add(idx2);
			}
			if (tmp.size() > 0) atomMapping.add(tmp);
			
			// If there is only one bond, check if it matches both ways.
			if (list.size() == 1 && atom1.getAtomicNumber() == atom2.getAtomicNumber()) {
				List<Integer> tmp2 = new ArrayList<Integer>();
				tmp2.add(tmp.get(0));
				tmp2.add(tmp.get(1));
				atomMapping.add(tmp2);
			}
		}
		return atomMapping;
	}
}
