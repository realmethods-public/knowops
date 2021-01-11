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

import java.util.HashMap;
import java.util.Map;

import com.realmethods.foundational.common.FrameworkBaseObject;

/** 
 * Abstract base class of all properties handlers.
 * <p>
 * Provides an internal cache for property storage.
 * <p>
 * @author  	realMethods, Inc.  
 * @see			com.realmethods.foundational.integration.cache.FrameworkCache
 */
public abstract class PropertiesHandler 
    extends FrameworkBaseObject implements IPropertiesHandler
{

// attributes   

	public PropertiesHandler()
	{
	}
		
// cache helper methods	
	
	/**
	 * Assigns the properties to the cache, referred to by the key.
	 * <p>
	 * @return	key
	 * @return	props
	 */
	protected void cache( Object key, Object props )
	{
		map.put( key, props );
	}
    
    /**
     * Returns the referred to object, or null if not in cache.
     * <p>
     * @param 	key
     * @return	referenced object
     */
	protected Object cache( Object key )
	{
		return( map.get( key ) );
	}
	
// attributes
    
    /**
     * internal properties cache
     */
    protected final transient Map<Object,Object> map = new HashMap<>();
    
}
