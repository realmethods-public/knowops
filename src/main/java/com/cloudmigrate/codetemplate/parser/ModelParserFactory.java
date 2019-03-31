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

import com.cloudmigrate.codetemplate.*;
import com.cloudmigrate.entity.ModelType;
import com.cloudmigrate.server.service.EMFHandler;
import com.cloudmigrate.server.service.IModelParser;
import com.cloudmigrate.server.service.JsonHandler;
import com.cloudmigrate.server.service.PojoHandler;
import com.cloudmigrate.server.service.SqlScriptHandler;
import com.cloudmigrate.server.service.XMIHandler;

/**
 * Model Parser Factory to provide a central Factory metaphor in creating
 * a model parser of the appropriate type.
 * 
 * @author realMethods, Inc.
 *
 */
public class ModelParserFactory extends AppGenObject
{
	/**
	 * Default constructor
	 */
	protected ModelParserFactory(){
		// no_op
	}
	
	/**
	 * Returns one and only instance of a ModelParserFactory
	 * @return ModelParserFactory
	 */
	public static ModelParserFactory getInstance() {
		if ( self == null )
			self = new ModelParserFactory();
		
		return( self );
	}
	
	/**
	 * Method that deduces from a files extension, the correct IModelParser to invoke.
	 * 
	 * @param fileName
	 * @return IModelParser
	 */
	public IModelParser modelParser( String fileName ) {
		
		return( modelParser(ModelType.deduceTypeFromFileName(fileName)));
	}

	/**
	 * Method that deduces from a files extension, the correct IModelParser to invoke.
	 * 
	 * @param fileName
	 * @return IModelParser
	 */
	public IModelParser modelParser( ModelType modelType ) {
		switch( modelType ) {
			case XMI 		: return new XMIHandler();
			case UML 		: return new XMIHandler();
			case ECORE 		: return new EMFHandler();
			case POJO 		: return new PojoHandler();
			case SQL_SCRIPT : return new SqlScriptHandler();
			case JSON 		: return new JsonHandler();
			default 		: return null;
		}
	}

	
	
	/**
	 * Internally delegates to createJavaDirParser( String, String)
	 *  
	 * @param 	classRootDir
	 * @return	JavaDirParser
	 */
	public JavaDirParser createJavaDirParser( String classRootDir ) {
		return createJavaDirParser( classRootDir, null );
	}
 
	/**
	 * Instantiates and returns a JavaDirParser.
	 * 
	 * @param classRootDir
	 * @param rootPackageName
	 * @return
	 */
	public JavaDirParser createJavaDirParser( String classRootDir, String rootPackageName )
	{
		return new JavaDirParser( classRootDir, rootPackageName );
	}

	/**
	 * Instantiates and returns an XMLFileParser.
	 * 
	 * @param classRootDir
	 * @param rootPackageName
	 * @return
	 */	
	public XMLFileParser createXMLFileParser( String xmlFileName )
	{
		return new XMLFileParser( xmlFileName );
	}
	
// attributes
	
	private static ModelParserFactory self = null;
}
