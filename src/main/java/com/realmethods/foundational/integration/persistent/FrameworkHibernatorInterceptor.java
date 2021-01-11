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
package com.realmethods.foundational.integration.persistent;

import java.io.Serializable;

import org.hibernate.*;
import org.hibernate.type.*;

import com.realmethods.foundational.common.FrameworkBaseObject;

/**
 * This class is provided to create a new Hibernate Session in order to apply
 * a time stamp to any Auditable class being persisted.  This is 
 * a good place to put other audit trail functionality related to 
 * persistence actions.
 * 
 * @author realMethods, Inc.
 */
public class FrameworkHibernatorInterceptor
	extends EmptyInterceptor 
	implements Serializable 
{

	@Override
	public boolean onFlushDirty(Object entity, 
								Serializable id, 
								Object[] currentState,
								Object[] previousState,
								String[] propertyNames,
								Type[] types) 
	{
		
		if ( entity instanceof com.realmethods.foundational.integration.persistent.Auditable ) 
		{
			
			for ( int i=0; i < propertyNames.length; i++ ) 
			{
				if ( "lastUpdateTimestamp".equals( propertyNames[i] ) ) 
				{
					new FrameworkBaseObject().logDebugMessage( "Applying lastUpdateTimeStamp to " + entity.toString() );					
					currentState[i] = new java.util.Date();
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean onLoad(Object entity, 
						  Serializable id,
						  Object[] state,
						  String[] propertyNames,
						  Type[] types) 
	{		
		if ( entity instanceof Persistent )
		{
			((Persistent)entity).onLoad();
			return( true );
		}
		else
			new FrameworkBaseObject().logDebugMessage( "FrameworkHibernatorInterceptor:onLoad() - object doesn't implemente Persistent interface : " + entity.toString() );

		return( false );
	}

	@Override
	public boolean onSave(Object entity,
						  Serializable id,
						  Object[] state,
						  String[] propertyNames,
						  Type[] types) 
	{   
		boolean saveIndicator = false;
		
		if ( entity instanceof Auditable ) 
		{
			for ( int i=0; ((i<propertyNames.length) && !(saveIndicator)); i++ ) 
			{
				if ( "createTimestamp".equals( propertyNames[i] ) ) 
				{
					new FrameworkBaseObject().logDebugMessage( "Applying createTimeStamp to " + entity.toString() );					
					state[i] = new java.util.Date();
					saveIndicator = true;
				}
			}						
		}
		else
			new FrameworkBaseObject().logDebugMessage( "FrameworkHibernatorInterceptor:onSave() - object doesn't implement Auditable interface : " + entity.toString() );
		
		if ( !saveIndicator )		
			saveIndicator = checkOnSave( entity );
		else
			checkOnSave( entity );
						
		return saveIndicator ;
	}


	public Boolean isUnsaved(Object entity)
	{		
		boolean unsaved = true;
		
		if ( entity instanceof Persistent ){
			unsaved = !((Persistent)entity).isSaved();
		}
		
		return( unsaved );
	}
	
	@Override
	public int[] findDirty(Object entity,
						   Serializable id,
						   Object[] currentState,
						   Object[] previousState,
						   String[] propertyNames,
						   Type[] types)
	{
		// use Hibernate's default dirty checking algorithm
		return( null );	
	}
	
	protected boolean checkOnSave( Object entity )
	{	
		if ( entity instanceof Persistent )
		{			
			((Persistent)entity).onSave();
			return( true );
		}
		else
			new FrameworkBaseObject().logDebugMessage( "FrameworkHibernatorInterceptor:checkOnSave() object doesn't implement Persistent interface : " + entity.toString() );

		return( false );		
		
	}
 }
 
