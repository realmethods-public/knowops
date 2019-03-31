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
package com.cloudmigrate.codetemplate.model.factory;

import java.lang.reflect.Method;

import org.eclipse.emf.ecore.EOperation;
import org.w3c.dom.Node;

import com.cloudmigrate.codetemplate.model.method.MethodObject;
import com.cloudmigrate.codetemplate.model.method.ClassMethodObject;
import com.cloudmigrate.codetemplate.model.method.EcoreMethodObject;
import com.cloudmigrate.codetemplate.model.method.NodeMethodObject;

/**
 * Method Factory used for creating MethodObjects derived from the different supported model sources
 * 
 * @author realMethods, Inc.
 *
 */
public class MethodFactory 
{
	/**
	 * intentionally externally inaccessible
	 */
	protected MethodFactory()
	{}
	
	/**
	 * Singleton factory pattern
	 * @return
	 */
	public static MethodFactory getInstance()
	{
		if ( self == null )
			self = new MethodFactory();
		
		return( self );
	}
	
	/**
	 * Creates a single instance of MethodObject from a DOM Node.
	 * 
	 * @param node
	 * @return
	 */
    public MethodObject createInstance( Node node ) {
		NodeMethodObject methodObject = new NodeMethodObject( node );
		return( methodObject );
	 }
	
	/**
	 * Creates a single instance of a MehodObject from a Java Method.
	 * 
	 * This method is a no_op.
	 * 
	 * @param method
	 * @return
	 */
    public MethodObject createInstance( Method method ){
		ClassMethodObject methodObject = new ClassMethodObject(method);		
		return( methodObject );
	}
	
	public MethodObject createInstance( EOperation operation )
	{
		EcoreMethodObject methodObject = new EcoreMethodObject( operation );
		return( methodObject );
	}
	
// attribute
	private static MethodFactory self 					= null;
}
