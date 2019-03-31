/*******************************************************************************
 * realMethods Confidential
 * 
 * 2018 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.cloudmigrate.foundational.business.bo;


/**
 * Base class for all business objects as generated and/or used by the Framework.
 * Such objects are simply used to encapsulate business data and not business processing, although
 * intraobject validation should be seen as acceptable.   
 * <p>
 * Subclass FrameworkValueObject for backwards compatibility, but will take
 * on its functionality in the near future. 
 * <p>
 * @author    realMethods, Inc.
 */
public abstract class FrameworkBusinessObject 
   implements IFrameworkBusinessObject
{
//***************************************************   
// Public Methods
//***************************************************
    /**
     * Default constructor.
     */
    public FrameworkBusinessObject( )
    {
    	super();
    }    

    
}

/*
 * Change Log:
 * $Log: FrameworkBusinessObject.java,v $
 */
