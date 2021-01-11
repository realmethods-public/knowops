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

import com.realmethods.codetemplate.xmi.IXMIProvider;
import com.realmethods.codetemplate.xmi.XMIProviderFactory;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.factory.*;
import com.realmethods.codetemplate.model.subsystem.SubsystemObject;


import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point into the goFramework to handle parsing a XMI model into the goFramework
 * Generation-Time model.
 * 
 * @author realMethods, Inc.
 *
 */
public class XMIParser extends XMLFileParser
{

	/**
	 * Constructor 
	 * 
	 * @param fileName
	 */
    public XMIParser( String fileName ) {
    	super( fileName );
    }

    @Override
    /**
     * Action overload to handle the actual loading and parsing of the XMI file.
     * 
     */
    protected void doRun() throws ProcessingException {
        try {
	        loadFromXMIFile();	        
	    }
	    catch(Exception exc) {
	    	throw new ProcessingException( "XMIParser.doRun()", exc );
	    }
    }
    
    /**
     * Parses the provided file name to determine if it is in fact a comma delimited list of files.
     * If so, then each is parsed and loaded into the model in the order tokenized. 
     *
     * @throws ProcessingException
     */
    protected void loadFromXMIFile() throws ProcessingException
    {
        String theFileName = null;

        // parse the xmiFileName to see if it is a concatenation of multiple files, 
        // separated by a ','
        
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( getXMLFileName(), "," );
        while( tokenizer.hasMoreTokens() ) {
        	theFileName = tokenizer.nextToken();
        	xmiLoad(theFileName);
        }
    }

    /**
     * Does the work turning the XMI file contents into a DOM.  It then shares
     * the root Document Node with the XMIProvideFactor to get the XMIProvider that
     * can actually handle parsing the format of the document.
     * 
     * @param xmiFileName
     * @throws ProcessingException
     */
    public void xmiLoad(String xmiFileName) throws ProcessingException
    {
        if(xmiFileName != null && !xmiFileName.isEmpty()) {        	
        	try {
        		
        		XMIParser.createXMIDocument(xmiFileName);
        		
	            xmiProvider		= XMIProviderFactory.getInstance(xmiDocument).getXMIProvider();
	            
	            // we first get the root level stuff, which in turn recursively can
	            // get at each of their relevant children
	            processRootSubsystems();
	            processRootComponents();
	            processRootClasses();
	            processRootInterfaces();
	            processRootEnums();
        	} catch( Exception exc ) {
        		final String msg = "Error in parsing model using file " + xmiFileName;
        		LOGGER.severe(msg);
        		throw new ProcessingException( "XMIParser.xmiLoad", exc);
        	}
        } 
        else
        {
        	final String msg = "Invalid XML file designation provided for input arg " + xmiFileName;
            LOGGER.log(Level.SEVERE, msg );
        }
    }
    
    /**
     * Helper methods to handle processing of the root nodes of the DOM.
     *  
     * @throws ProcessingException
     */
    protected void processRootSubsystems()
    		throws ProcessingException
    {
        LOGGER.info("Querying Model for Root Level Subsystems");

        try {
	    	String nodePath 		= xmiProvider.getNodeXMIDecl();
	    	
	    	if ( nodePath == null )
	    		return;

	        NodeList nodeList 		= xmiProvider.getXPathAPI().selectNodeList(xmiDocument, nodePath);
	        Node rootNode			= null;
	        
	        for( int index=0; index < nodeList.getLength(); index++ )
	        {	
	        	rootNode = nodeList.item(index);
	        	SubsystemObject node = SubsystemFactory.getInstance().createSubsystemObject( rootNode );
	        	ModelParser.modelParser().addSubsystem( node );
	        }
        } catch (Exception exc ) {
        	LOGGER.log( Level.SEVERE, "Failure during processing Subsystems", exc);
        	throw new ProcessingException( "Failure during processing Subsystems", exc );
        }
    }

    /**
     * Helper methods to handle processing of the root components of the DOM.
     *  
     * @throws ProcessingException
     */
    protected void processRootComponents()
    		throws ProcessingException
    {
        LOGGER.info("Querying Model for Root Level Components");

        try {
	    	String componentPath 	= xmiProvider.getComponentXMIDecl();

	    	if ( componentPath == null )
	    		return;

	        NodeList nodeList 		= xmiProvider.getXPathAPI().selectNodeList(xmiDocument, componentPath);
	        Node componentNode 		= null;
	        
	        for( int index=0; index < nodeList.getLength(); index++ )
	        {	
	        	componentNode = nodeList.item(index);
	        	ModelParser.modelParser().addComponent(ComponentFactory.getInstance().createComponentObject( componentNode));
	        }
        } catch (Exception exc ) {
        	LOGGER.log( Level.SEVERE, "Failure during processing Components", exc);
        	throw new ProcessingException("Failure during processing Components", exc);
        }	        
    }

    /**
     * Helper methods to handle processing of the root classes of the DOM.
     *  
     * @throws ProcessingException
     */
    protected void processRootClasses()
    		throws ProcessingException
    {
        LOGGER.info("Querying Domain Object Model for Root Level Classes");

        try {
	    	String classPath 		= xmiProvider.getClassXMIDecl();
	    	
	    	if ( classPath == null )
	    		return;
	    	
	        NodeList nodeList 	= xmiProvider.getXPathAPI().selectNodeList(xmiDocument, classPath);
	        Node classNode 		= null;
	        
	        for( int index=0; index < nodeList.getLength(); index++ )
	        {
	        	classNode = nodeList.item(index);
	        	ModelParser.modelParser().addClass(ClassFactory.getInstance().createClassObject( classNode));
	        }
        } catch (Exception exc ) {
        	LOGGER.log( Level.SEVERE, "Failure during processing root Classes", exc);
        	throw new ProcessingException("Failure during processing root Classes", exc);
        }	        
	        
    }

    /**
     * Helper methods to handle processing of the root interfaces of the DOM.
     *  
     * @throws ProcessingException
     */
    protected void processRootInterfaces()
    		throws ProcessingException
    {
        LOGGER.info("Querying Domain Object Model for Root Interfaces");

        try {
	    	String interfacePath 	= xmiProvider.getInterfaceXMIDecl();
	    	
	    	if ( interfacePath == null )
	    		return;
	    	
	        NodeIterator nodeIter 	= xmiProvider.getXPathAPI().selectNodeIterator(xmiDocument, interfacePath);
	        Node interfaceNode 		= null;
	        
	        while ((interfaceNode = nodeIter.nextNode()) != null )
	        {
	        	ModelParser.modelParser().addInterface(ClassFactory.getInstance().createClassObject(interfaceNode));
	        }
        } catch( Exception exc ) {
        	LOGGER.log( Level.SEVERE, "Failure during processing root Interfaces", exc);
        	throw new ProcessingException("Failure during processing root Interfaces", exc);
        }
         
    }

    /**
     * Helper methods to handle processing of the root Enumerations of the DOM.
     *  
     * @throws ProcessingException
     */
    protected void processRootEnums()
    		throws ProcessingException
    {
        LOGGER.info("Querying Domain Object Model for Root Level Enumerations");

        try {
	    	String enumPath = xmiProvider.getEnumXMIDecl();
	    	
	    	if ( enumPath == null || enumPath.isEmpty() )
	    		return;
	    	
	        NodeList nodeList 	= xmiProvider.getXPathAPI().selectNodeList(xmiDocument, enumPath);
	        Node classNode 		= null;
	        
	        for( int index=0; index < nodeList.getLength(); index++ )
	        {
	        	classNode = nodeList.item(index);
	        	ClassObject classObject = ClassFactory.getInstance().createClassObject( classNode);
	        	classObject.setStereotype("enum");
	        	ModelParser.modelParser().addClass(classObject);
	        }
        } catch (Exception exc ) {
        	LOGGER.log( Level.SEVERE, "Failure during processing root Enums", exc);
        	throw new ProcessingException("Failure during processing root Enums", exc);
        }	        
	        
    }

    /**
     * Returns the xmiDocument field. 
     * 
     * Null is a possibility if called before processing has begun or if processing failed due to being unable to 
     * create the DOM.
     * 
     * @return Document
     */
    public static Document xmiDocument() {
    	return xmiDocument;
    }
    
    /**
     * internal static helper to assign the xmiDocument static attribute
     * 
     * @param xmiFileName
     */
    protected static void createXMIDocument( String xmiFileName ) {
    	try {
			InputStream inputStream = AppGenHelper.determineInputStream( xmiFileName );
			
	        InputSource in = new InputSource(inputStream);
	        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
	        dfactory.setNamespaceAware(true);
	        
	        xmiDocument 	= dfactory.newDocumentBuilder().parse(in);
    	} catch( Exception exc ) {
    		LOGGER.log( Level.WARNING, "Failed to create the XMIM Document", exc);
    	}
    }
    
// attributes

    protected static Document xmiDocument		= null;
    protected IXMIProvider xmiProvider 			= null;
	private static final Logger LOGGER 			= Logger.getLogger(XMIParser.class.getName());

}
