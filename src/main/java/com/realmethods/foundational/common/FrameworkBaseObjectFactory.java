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
package com.realmethods.foundational.common;

import java.beans.Beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all instances of object factories used within the Framework.
 * <p>
 * Has the ability to conditionally cache the objects created.
 * <p> 
 * @author    realMethods, Inc.
 */
public class FrameworkBaseObjectFactory  
    extends FrameworkBaseObject
{

// constructors

    /**
     * default constructor - inaccessible 
     */
    private FrameworkBaseObjectFactory()
    {
    }
    
    /**
     * deter external creation - only accessible constructor through subclass
     *
     * @param	doCache		indicator whether or not to store created objects
     */
    protected FrameworkBaseObjectFactory( boolean doCache )
    {
    	bCache  = doCache;
    	
    	if( bCache )
    	{
			objects = Collections.synchronizedMap( new HashMap() );
    	}
    }
    
// access methods

    /**
     * returns the Class representation of the named class
     *
     * @param       className		fully qualified name of class to create
     * @return      Class
     */
    protected Class getClass( String className )
    {
		if ( className == null || className.length() == 0 )
			return( null );
		    	
        Class theClass = null;
        
        try {
            theClass = Class.forName( className );
        }
        catch( ClassNotFoundException cnfExc ) {
            logErrorMessage( "FrameworkBaseObjectFactory:getClass() - unable to get the class " + className + " - " + cnfExc );
        }
        
        return( theClass );
    }

    /**
     * returns the Object representation of the named class
     *
     * @param       className		fully qualified name of class to create
     * @return      Class
     */
    protected Object getObject( String className )
    {
    	if ( className == null || className.length() == 0 )
    		return( null );
    	
        Object object = null;
        
        if ( bCache ) // do we cache
        {
            // has it been cached
            object = objects.get( className );            
        }
        
        // null by default, or not found in the cache
        if ( object == null )
        {
            try
            {            	
                object = getObjectHelper( getClass().getClassLoader(), className );
                
                if ( object != null )
                {
                    if ( bCache )
	                    objects.put( className, object );                                      
                }
                else
                	throw new InstantiationException( "FrameworkBaseObjectFactory:getObject() - failed to create class " + className + ".  Make sure it is made available." );
            }
            catch( Exception exc )
            {
                logInfoMessage( "FrameworkBaseObjectFactory:getObject() - " + exc );
            }
        }     
        
        return( object );
    }
    
    /**
     * Recursive method used to load className.  If the provided class loader doesn't work, attempts to
     * its parent.
     * <p>
     * @param classloader
     * @param className
     * @return	creataed objected by className
     */
    protected Object getObjectHelper( ClassLoader classloader, String className )
	{
		Object target = null;
				
		if ( classloader != null )		
		{		
			try
			{
				target = Beans.instantiate( classloader, className );            		
			}
			catch( ClassNotFoundException exc )
			{
				logErrorMessage( "FrameworkBaseObjectFactory::getObjectHelper() - Failed to locate class " + className + " with current class loader, but will try it's parent class loader - " + exc );        		        	
				getObjectHelper( classloader.getParent(), className );    	
			}
			catch( Exception throwable )
			{        	        	
				logErrorMessage( "FrameworkBaseObjectFactory::getObjectHelper() - " + className + " - exception caught using classloader " + classloader.toString() + "- " + throwable );        	
			} 
		}
		        
		return( target );
	}
 
// action methods

    /**
     * Clear the maintenance of cached objects.
     */
    public synchronized void clearCache()
    {
        if ( bCache )
        {
            objects.clear();
        }
    }
    
    /**
     * Client affirmation of being done with an object created by the factor.
     * <p>
     * Only of use if caching is turend on.
     * <p>
     * @param		object		entity to remove from the cache, if caching  is enabled
     */
    public void release( Object object )
    {
        // if we are supposed to have cache and have it, remove it
        if ( bCache && objects.containsValue( object ) )
        {
            objects.remove( object );
        }
    }    
    
// protected methods

	    
// attributes

    /**
     * store for class name/class instance pairings if caching is turned on
     */
    protected transient Map  objects	= null;
    
    /**
     * cache indicator - caching is disabled by default
     */
    protected transient boolean bCache    	= false;
    
}

/*
 * Change Log:
 * $Log: FrameworkBaseObjectFactory.java,v $
 */
