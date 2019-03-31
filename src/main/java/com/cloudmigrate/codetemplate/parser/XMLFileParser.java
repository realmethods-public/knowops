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
package com.cloudmigrate.codetemplate.parser;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.foundational.common.exception.ProcessingException;

/**
 * Represents the main class for any scenario requiring building the goFramework model
 * from an XML file.  As of now, it is assumed to be an XMI file format, so this method
 * delegates internally to an XMIParser.
 * 
 * @author realMethods, Inc.
 *
 */
public class XMLFileParser extends BaseModelParser
{

	/**
	 * Only constructor, requires a valid fileName.
	 * 
	 * @param fileName
	 */
    public XMLFileParser( String fileName )
    { 
    	xmlFileName = fileName;
    }
    
    @Override
    protected void doRun() throws ProcessingException
    {
    	try
    	{
	    	XMIParser parser = new XMIParser( xmlFileName );
	    	parser.doRun();
    	}
    	catch( Exception exc ) {
    		final String msg = "Failed to run the XMI Parser with file name " + xmlFileName;
    		LOGGER.log( Level.SEVERE, msg, exc );
    		throw new ProcessingException( msg, exc);
    	}
    }


    /**
     * Returns the xmlFileName field.
     * @return
     */
    public String getXMLFileName()
    {
        return xmlFileName;
    }

    /**
     * Assigns the fileName field the provided argument.
     * 
     * @param fileName
     */
    public void setXMLFileName(String fileName)
    {
        xmlFileName = fileName;
    }

    // attributes
    protected String xmlFileName		= null;
 	private static final Logger LOGGER 	= Logger.getLogger(XMLFileParser.class.getName());

}
