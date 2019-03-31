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
package com.cloudmigrate.entity;

import com.cloudmigrate.foundational.business.bo.FrameworkHibernateEnumerator;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.util.*;

/**
 * Hibernate wrapper class in order to be able to persist a ScopeType object.
 * 
 * @author realMethods, Inc.
 *
 */
public class ScopeTypeEnumType
    extends FrameworkHibernateEnumerator<com.cloudmigrate.entity.ScopeType>
    implements java.io.Serializable
{
//************************************************************************
// Constructors
//************************************************************************
    /**
     * Default Constructor - for reflection purposes only
     */
    public ScopeTypeEnumType()
    {
        super(com.cloudmigrate.entity.ScopeType.class);
    }
}
