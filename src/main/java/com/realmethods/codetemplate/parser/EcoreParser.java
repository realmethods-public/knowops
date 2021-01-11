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
package com.realmethods.codetemplate.parser;

import com.realmethods.codetemplate.emf.AIBEMFHelper;
import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.factory.*;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.codetemplate.model.classes.enumerate.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.*;
import org.eclipse.emf.ecore.util.*;
import org.eclipse.emf.ecore.xml.type.*;


/**
 * Core class for handling parsing of an Eclipse EMF file, normally with either
 * a .ecore or .core extension.  The extension is ignored here.
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreParser extends XMLFileParser
{
	/**
	 * Default constructor
	 * 
	 * Delegates to the super class
	 * 
	 * @param ecoreFileName
	 */
    public EcoreParser( String ecoreFileName )
    {
    	super( ecoreFileName );
    }
   
    @Override
    /**
     * Handles the parsing and creation of the internal goFramework model from an Eclipse EMF model,
     */
    protected void doRun() throws ProcessingException
    {
        try
        {
        	ecoreLoad();
        	finishLoading();
	    }
	    catch(Exception exc)
	    {
	    	LOGGER.log(Level.WARNING, "EcoreParser.doRun()", exc);
	    	throw exc;
	    }

    }

    public void ecoreLoad()
    {
    	LOGGER.log(Level.INFO, "Starting to parse and load the EMF file {0}", getXMLFileName());

    	if(getXMLFileName() != null && !getXMLFileName().isEmpty())
    	{
    		for( org.eclipse.emf.ecore.EObject eObjectImpl : 
    			AIBEMFHelper.getInstance().getRootResource( getXMLFileName() ).getContents() )
    			processEObjectImpl( eObjectImpl );
	    } 
    	else {
    		LOGGER.log(Level.WARNING, "Invalid model file provided - {0}", this.xmlFileName);
	    }
    }	

    /**
     * Helper method to process parsing an Eclipse EMF object
     * 
     * @param eObjectImpl
     */
    public void processEObjectImpl( org.eclipse.emf.ecore.EObject eObject )
    {
    	LOGGER.log(Level.INFO, "EObject is {0}", eObject);
    	if ( eObject instanceof EPackage )
    		processPackage( (EPackage)eObject );
    	else if ( eObject instanceof org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelImpl ) {
    		eObject.eContents().forEach( e -> processPackage( 
    				((org.eclipse.emf.codegen.ecore.genmodel.impl.GenPackageImpl)e).getEcorePackage()));
    	}
    }
        
    /**
     * Process the entire contents of an EPackage object.
     * 
     * @param EPackageImpl
     */
    protected void processPackage( EPackage pkg) {

    	pkg.eContents().forEach( eObjectImpl ->  {
    		LOGGER.log(Level.INFO, "Contents of EPackageImpl pkg is {0}", eObjectImpl.eContents());
			
        	if ( eObjectImpl instanceof EClassImpl ) {
        		processClass((EClassImpl)eObjectImpl);
        	}
        	else if ( eObjectImpl instanceof EEnum ) {
        		processEnum((EEnum)eObjectImpl);
        	}
    	});
    }

    protected void processEnum( EEnum eenum) {
		LOGGER.log(Level.INFO, "Contents of eenum {0}", eenum.eContents());

    	ClassObject classObject = ClassFactory.getInstance().createInstance(eenum);
		ModelParser.modelParser().addEnum( new EnumClassObject(classObject) );
    }

    protected void processClass( EClass eClass ) {
		LOGGER.log(Level.INFO, "Contents of eClass {0}", eClass.eContents());
    	
    	ClassObject classObject = ClassFactory.getInstance().createInstance(eClass);

		if ( !eClass.isInterface() )
			ModelParser.modelParser().addClass( classObject );
		else
			ModelParser.modelParser().addInterface( classObject );

    }

    /**
     * Notifies each child in the model that loading has come to an end.
     * 
     * This method should be moved to a more central location.
     */
    protected void finishLoading()
    {
    	for( BaseModelObject baseModelObject : ModelParser.modelParser().getAllInHierarchy() ) {
    		baseModelObject.finishLoading();
    	}
    }

    // attributes
	private static final Logger LOGGER = Logger.getLogger(EcoreParser.class.getName());

}
