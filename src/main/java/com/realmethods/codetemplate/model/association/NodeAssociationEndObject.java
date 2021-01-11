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
package com.realmethods.codetemplate.model.association;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import com.realmethods.codetemplate.xmi.IXMIProvider;
import com.realmethods.codetemplate.xmi.XMIProviderFactory;
import com.realmethods.common.helpers.Utils;

/**
 * Binds an association end with an DOM Node. 
 * Extracts data from the Node to store in parent AssociationEndObject
 * 
 * @author realMethods, Inc.
 *
 */
public class NodeAssociationEndObject extends AssociationEndObject
{
	/**
	 * Default constructor
	 */
	public NodeAssociationEndObject() {
		// no_op
	}
	
	/**
	 * Extracts the relevant data from the provided DOM Node
	 * 
	 * @param node
	 * @param findSibling
	 */
    public void acquireAssociationEndInfo( Node node, boolean findSibling ) {
        if( node == null ) {
        	LOGGER.severe( "The input node arg is null..." );
            return;
        }

    	IXMIProvider xmiProvider 	= XMIProviderFactory.getInstance().getXMIProvider();
        String roleName 			= xmiProvider.getAssociationRoleName(node);
        int [] range 				= xmiProvider.getLowerUpperRange(node);

        // apply node data to parent attributes

        setType( xmiProvider.getAssociationEndType(node) );
        
        // determine how to apply the role name
        if (roleName == null || roleName.length() ==  0 || roleName.equalsIgnoreCase( "none") )
        	roleName = getType();

        roleName = Utils.lowercaseFirstLetter( roleName );
        
        setRoleName( roleName );
        setVisibility( "public" );
        setTags( xmiProvider.getTagNamesAndValues(node));
        setRanges( range[0], range[1] );

        isNavigable( xmiProvider.getNavigableIndicator(node) );
        isOrdered( xmiProvider.getOrderedIndicator(node) );
        isAggregate( xmiProvider.getAggregationIndicator(node) );
        isComposite( xmiProvider.getCompositeIndicator(node) );
        isFrozen( xmiProvider.getFinalIndicatorForAssociation(node) );
        isAddOnly( xmiProvider.getAddOnlyIndicatorForAssociation(node) );

        if( findSibling ) {
            try {
                siblingAssociationEndObject = xmiProvider.getAssociationSibling(node);
                siblingAssociationEndObject.setSiblingAssociationEndObject(this);
            }
            catch(Exception exc) {
            	LOGGER.log( Level.WARNING, "Failed to create sibling AssociationEndObject ", exc );
            }
        }
    }

// attributes
    private static final Logger LOGGER 	= Logger.getLogger(NodeAssociationEndObject.class.getName());
}
