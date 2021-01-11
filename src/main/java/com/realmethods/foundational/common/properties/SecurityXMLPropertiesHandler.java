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
package com.realmethods.foundational.common.properties;

//***************************
//Imports
//***************************
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Interface to the provider of property access to the contents of the security related config file.
 * <p>
 * @author		realMethods, Inc.
 */
public class SecurityXMLPropertiesHandler 
	extends XMLPropertiesHandler
	implements ISecurityPropertiesHandler
{

//************************************************************************    
// Public Methods
//************************************************************************
 
   /**
   	* Returns a Collection of Strings, representing the names of the security managers.
   	* @return	security manager names
   	*/
   	public Collection getSecurityManagerNames()
   	{
		return( getFrameworkXMLParser().getAttributesForEachOccurance( "sec-manager", "name" ) );
   	}

   /**
	* Returns the attribute names and values for the provided security manager name.
	* @param 	secMgrName
	* @return	properties for the named security mananger
	*/
   	public Map getSecurityManagerParams( String secMgrName )
   	{
		Map params = getFrameworkXMLParser().getAttributesForEachOccuranceBy( "sec-manager",
																		"name",
																		secMgrName );    	
		return( params );   		
   	}
  
	/**
	 * Returns a Map where the key is the name of a sec. manager, and the value is a Map
	 * of its values.
	 * <p>
	 * @return	all security manager properties, where the key is a sec. mgr. name, and the
	 * 			related value is a Map of its properties 
	 */
	public Map getSecurityManagers()
	{
		Map secMgrs = (Map)cache( "securityManagers" );		
		
		if ( secMgrs == null )
		{
			secMgrs = new HashMap();
			Collection names 	= getSecurityManagerNames();
			Iterator iter		= names.iterator();
			String name			= null;
				
			while( iter.hasNext() )
			{
				name = (String)iter.next();
				secMgrs.put( name, getSecurityManagerParams( name ) );
			}
			
			cache( "securityManagers", secMgrs );
		}
		
		return( secMgrs );
    }  
    
//************************************************************************    
// Attributes
//************************************************************************

}

/*
 * Change Log:
 * $Log$
 */
