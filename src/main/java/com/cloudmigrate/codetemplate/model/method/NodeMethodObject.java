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
package com.cloudmigrate.codetemplate.model.method;

import org.w3c.dom.Node;

import com.cloudmigrate.codetemplate.xmi.IXMIProvider;
import com.cloudmigrate.codetemplate.xmi.XMIProviderFactory;

/**
 * Wrapper class to support the required MethodObject type while extracting data from a DOM Node method representation
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeMethodObject extends MethodObject
{
	/**
	 * Intentionally protected 
	 */
	protected NodeMethodObject()
	{}
	
	/**
	 * Constructor.
	 * 
	 * Binds the Node with this instance.  The relevant XMIProvider contains the
	 * capabilities to help the instance populate itself.
	 * @param node
	 */
    public NodeMethodObject(Node node)
    {
        xmiProvider = XMIProviderFactory.getInstance().getXMIProvider();
        
        setName( xmiProvider.getElementName(node) );
        setVisibility( getVisibility(node) );
        setArguments( getArguments(node) );
        setDocumentation( getDocumentation(node) );
        setReturnType( this.arguments.getReturnType() );
        isStatic( isFromStatic(node) );

        reconcileReturnType();
        reconcileArguments();
    }

    /**
     * Helper method.
     * 
     * Returns the visibility of the model method.
     * 
     * @param methodNode
     * @return String
     */
    private String getVisibility(Node methodNode)
    {
        return xmiProvider.getVisibility(methodNode);
    }

    /**
     * Helper method.
     * 
     * Returns true/false of the modeled method being static.
     * 
     * @param methodNode
     * @return boolean
     */
    protected boolean isFromStatic(Node methodNode)
    {
        return xmiProvider.isMethodStatic(methodNode);
    }

    /**
     * Helper method.
     * 
     * Returns the documentation of the provided method.
     * 
     * @param methodNode
     * @return boolean
     */
    protected String getDocumentation(Node methodNode)
    {
        return xmiProvider.getDocumentation(methodNode);
    }

    /**
     * Helper method.
     * 
     * Returns the method's arguments as a MethodArgs instance.
     * 
     * @param methodNode
     * @return boolean
     */
    protected MethodArgs getArguments(Node methodNode)
    {
        return xmiProvider.getMethodArguments(methodNode);
    }

    @Override
    public boolean equals( Object object ) {

    	if ( !super.equals(object) )
    		return false;
    	
    	if (object == this) 
    		return true;
    	
    	if ( !(object instanceof NodeMethodObject) )
    	    return false;
  
    	
    	return( this.xmiProvider == ((NodeMethodObject)object).xmiProvider );
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }
    
// attributes
    private IXMIProvider xmiProvider;
}
