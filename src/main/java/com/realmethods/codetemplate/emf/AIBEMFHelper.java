/*******************************************************************************
 * realMethods Confidential
 * 
 * 2021 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.realmethods.codetemplate.emf;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.resource.Resource;

import com.realmethods.codetemplate.AppGenObject;


/**
 * Helper object to wrap usage of EMF related APIs
 * 
 * @author 	realMethods, Inc.
 */
public class AIBEMFHelper extends AppGenObject
{
// constructors
	
	protected AIBEMFHelper()
	{		
		// create resource set 
		resourceSet = new ResourceSetImpl();

		// Register XMI, XML, and EMF resource factories		

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "core", new EcoreResourceFactoryImpl());
		// do not remove this!!! files uploaded in prior version have a .tmp extension and may be passed directly for parsing
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "tmp", new EcoreResourceFactoryImpl());

	}

	/**
	 * singleton pattern - produces only one instance per JVM
	 * @return
	 */
	public static AIBEMFHelper getInstance()
	{
		return( new AIBEMFHelper() );
	}
	
	/**
	 * 
	 * @param fileResourceName
	 * @return
	 */
	public Resource getRootResource( String fileResourceName )
	{
		Resource root = null;
		
		try
		{			
			Map<String, Boolean> options 	= new HashMap<>();
			URI resource 					= URI.createFileURI( fileResourceName );
			
			options.put( XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
			options.put( XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);
			
			final String uriMsg = "AIBEMFHelper: URI Resource is " + resource.toString();
			LOGGER.log(Level.INFO, () -> uriMsg );
			
			root = resourceSet.getResource(resource, true);
			root.load( options );
			
			LOGGER.log(Level.INFO, "AIBEMFHelper: root Resource is {0}", root);
		}
		catch( Exception exc )
		{
			LOGGER.log(Level.WARNING, "Error creating resource: " + fileResourceName, exc );
		}
		
		return( root );
	}
	
// attributes
	
	protected ResourceSet resourceSet 	= null;
    private static final Logger LOGGER 	= Logger.getLogger(AIBEMFHelper.class.getName());
	
}
