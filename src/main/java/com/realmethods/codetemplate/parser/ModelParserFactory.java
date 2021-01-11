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

import com.realmethods.api.PojoParams;
import com.realmethods.codetemplate.*;
import com.realmethods.entity.ModelType;
import com.realmethods.server.service.EMFHandler;
import com.realmethods.server.service.GitHandler;
import com.realmethods.server.service.IModelParser;
import com.realmethods.server.service.JsonHandler;
import com.realmethods.server.service.PojoHandler;
import com.realmethods.server.service.SqlScriptHandler;
import com.realmethods.server.service.XMIHandler;
import com.realmethods.server.service.YamlHandler;

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
			case GIT		: return new GitHandler();
			case SQL_SCRIPT : return new SqlScriptHandler();
			case JSON 		: return new JsonHandler();
			case YAML		: return new YamlHandler();
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
	 * @param pojoParams
	 * @return
	 */
	public JavaDirParser createJavaDirParser( String classRootDir, PojoParams pojoParams )
	{
		return new JavaDirParser( classRootDir, pojoParams );
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
