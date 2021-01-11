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
package com.realmethods.codetemplate;

import com.realmethods.exception.GenerationException;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.realmethods.codetemplate.model.BaseModelObject;
import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.container.ContainerObject;
import com.realmethods.codetemplate.model.subsystem.SubsystemObject;
import com.realmethods.codetemplate.parser.ModelParser;
import com.realmethods.common.helpers.AWSHelper;
import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.entity.FrameworkPackage;
import com.realmethods.entity.Resource;
import com.realmethods.entity.User;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.ResourceDAO;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.output.FileWriterWithEncoding;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.*;

/**
 * Executable class used to handle the details surrounding generating all the
 * application files for a given technology stack package. Includes all file
 * parsing, template execution, and file output routing according to the
 * mappings found in the mapping.properties for a given technology stack
 * package.
 * 
 * @author realMethods, Inc.
 *
 */
public class GenerateApp {

	/**
	 * hidden default constructor
	 */
	protected GenerateApp() {
		// intentionally blank
	}

	/**
	 * sole public construction
	 */
	public GenerateApp(	String serverRootPath, 
						FrameworkPackage frameworkPackage, 
						GenerateAppOptions genAppOptions, 
						User user) {

		this.serverRootPath 	= serverRootPath;
		this.frameworkPackage 	= frameworkPackage;
		this.genAppOptions 		= genAppOptions;
		this.user 				= user;
		this.packageRootPath 	= determinePackageRootPath( frameworkPackage );

		commonTemplatesPath = determineCommonTemplates();

		LOGGER.log(Level.INFO, "Server root path: {0}", serverRootPath);
		LOGGER.log(Level.INFO, "Package root path: {0}", packageRootPath);

		velocityEngine = new VelocityEngine();

		// just in case, create it
		this.createOutputDir(serverRootPath);

		// first step is to handle any resources that are being referenced as an option to ensure
		// they are given priority over any other named file in the template paths
		handleApplyingResources();

		// handle property creation and assignment
		Properties p = handleAssigningProperties();

		// initialized the Velocity Template Engine with the Properties
		try {
			velocityEngine.init(p);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "GenerateApp constructor - Velocity Engine init failed", exc);
		}

		// store locally for use later
		context = new VelocityContext();

		// apply the Utils helper object for templates to leverage the
		// helper methods
		applyDataToContext("Utils", new com.realmethods.common.helpers.Utils());

		// put the packages in the velocity context
		applyDataToContext("packages", ModelParser.modelParser().getPackages());

		// place the velocity esc tool in the context
		applyDataToContext("esc", new org.apache.velocity.tools.generic.EscapeTool());

		// place the velocity math tool in the context
		applyDataToContext("math", new org.apache.velocity.tools.generic.MathTool());

		// for now...
		if (genAppOptions.getOptions().get("application.package name") == null)
			genAppOptions.getOptions().put("application.package name", "com.realmethods");

		genAppHelper = new GenerateAppHelper(null, this.genAppOptions.getOptions());

		// put the aib in the context
		applyDataToContext("aib", genAppHelper);

		// put the modelParser in the context
		applyDataToContext("model", ModelParser.modelParser());

		// put the current user in the context
		applyDataToContext("user", user);

	}

	/**
	 * Returns the app generation statistics, encapsulated in a GenerateAppStats
	 * instance
	 * 
	 * @return	application generation statistics
	 */
	public GenerateAppStats getGenerateAppStats() {
		return generateAppStats;
	}

	/**
	 * Allows for the external assignment of a key and associated object to the
	 * application generation session context
	 * 
	 * @param name 		the name to apply the object with within the project generation session context
	 * @param object    the object to apply within the project generation session context
	 */
	public void applyDataToContext(String name, Object object) {
		if (context != null)
			context.put(name, object);
	}

	/**
	 * method used to indicated by the instance user that this instance is no longer
	 * required
	 */
	public void cleanUp() {

		// ===============================================================
		// if the package is derivedFrom, the  a temporary package path
		// was created so delete it quietly
		// ===============================================================
		if (this.packageRootPath != frameworkPackage.getFilePath() ) {
			deleteQuietly(commonTemplatesPath);

		}

		if (this.commonTemplatesPath != null ) {
			deleteQuietly(commonTemplatesPath);

		}

		if (this.resourceOverridePath != null) {
			deleteQuietly(resourceOverridePath);
		}

	}

	/**
	 * set up method to handle all the prep necessary to do the app generation.
	 * Calls generateTheFiles() to do the heavy lifting
	 * 
	 * @throws	GenerationException		thrown in the event of a general exception during project generation
	 */
	public void generate() throws GenerationException {

		// contain all the classes in a single List
		ModelParser modelParser = ModelParser.modelParser();

		generateAppStats = new GenerateAppStats();

		// load the directory mappings to help to determine where to put a generated file
		try {
			loadDirMappings();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "load directory mappgings failed", exc );
		}

		// ==============================================================================================
		// this is where things get a bit interesting... all the root nodes are
		// in the model parser,
		// and each node contains it's children nodes.
		// ==============================================================================================

		// ==============================================================
		// so handle all the root nodes, that do not have children
		// this includes interfaces, enums, loose services, and classes
		// ==============================================================
		Map<UMLRootType, List<? extends BaseModelObject>> componentsToHandle = new EnumMap<>(UMLRootType.class);
		boolean somethingToHandle = false;
		if (!modelParser.getClasses().isEmpty()) {
			componentsToHandle.put(UMLRootType.CLASSES, assertNotNull(UMLRootType.CLASSES, modelParser.getClasses()));
			somethingToHandle = true;
		}
		if (!modelParser.getInterfaces().isEmpty()) {
			componentsToHandle.put(UMLRootType.INTERFACES,
					assertNotNull(UMLRootType.INTERFACES, modelParser.getInterfaces()));
			somethingToHandle = true;
		}
		if (!modelParser.getServices().isEmpty()) {
			componentsToHandle.put(UMLRootType.SERVICES,
					assertNotNull(UMLRootType.SERVICES, modelParser.getServices()));
			somethingToHandle = true;
		}
		if (!modelParser.getEnums().isEmpty()) {
			componentsToHandle.put(UMLRootType.ENUMS, assertNotNull(UMLRootType.ENUMS, modelParser.getEnums()));
			somethingToHandle = true;
		}

		LOGGER.log(Level.INFO, "GenerateApp.generate() - Entities involved in generation");

		componentsToHandle.values().forEach(component -> LOGGER.log(Level.INFO, component.toString()));

		// ==============================================================
		// if there are root entities (non container entities) in the model,
		// output them at the root level
		// if there are root entities (non container entities) in the model,
		// output them at the root level
		// ==============================================================
		if (somethingToHandle) {

			// ==============================================================
			// first, generate the files by parsing through the root of the
			// package path itself
			// ==============================================================
			this.overWriteFlag = true;
			this.setTheTemplatePathToUse(this.packageRootPath);
			generateTheFiles(this.genAppOptions.getWorkingDir(), componentsToHandle);

			// ==============================================================
			// next, generate the files by parsing the resource override path
			// to handle the files provided directly by the user, overwriting if already exists
			// ==============================================================
			this.overWriteFlag = true;
			this.setTheTemplatePathToUse(this.resourceOverridePath);
			generateTheFiles(this.genAppOptions.getWorkingDir(), componentsToHandle);

			// ==============================================================
			// next set the overwrite flag to false and parse through the common
			// package path
			// ==============================================================
			this.overWriteFlag = false;
			this.setTheTemplatePathToUse(this.commonTemplatesPath);
			generateTheFiles(this.genAppOptions.getWorkingDir(), componentsToHandle);
		}

		// ==============================================================
		// if containers, we still want minimal files to push the files to
		// gitHub and execute
		// a build from a parent maven pom file
		// ==============================================================
		if (!modelParser.getContainers().isEmpty()) {
			String antDir = commonTemplatesPath + File.separatorChar + BUILD + File.separatorChar + "ant"
					+ File.separatorChar;
			String mavenDir = commonTemplatesPath + File.separatorChar + BUILD + File.separatorChar + "maven"
					+ File.separatorChar;
			String gitDir = commonTemplatesPath + File.separatorChar + BUILD + File.separatorChar + "github"
					+ File.separatorChar;
			BaseModelObject emptyObject = new ClassObject();
			emptyObject.setName("empty");

			// ==============================================================
			// create the designated working directory
			// ==============================================================
			createOutputDir(this.genAppOptions.getWorkingDir());

			// ==============================================================
			// the template root location to use
			// ==============================================================
			this.setTheTemplatePathToUse(this.commonTemplatesPath);

			// ==============================================================
			// process the pom-parent.xml
			// ==============================================================
			generateTheFileHelper(mavenDir + File.separatorChar + POM_PARENT, POM_PARENT,
					this.genAppOptions.getWorkingDir(), emptyObject);

			// ==============================================================
			// rename it from pom-parent.xml to pom.xml for consistency
			// ==============================================================
			if (!new File(this.genAppOptions.getWorkingDir() + File.separatorChar + POM_PARENT)
					.renameTo(new File(this.genAppOptions.getWorkingDir() + File.separatorChar + POM_XML)))
				LOGGER.warning("Failed to rename " + POM_PARENT + " to " + POM_XML);

			// ==============================================================
			// do not want a container to have it so remove it from the loaded
			// mappings
			// ==============================================================
			this.dirMappings.remove(POM_PARENT);

			// ==============================================================
			// process the github.sh
			// ==============================================================
			generateTheFileHelper(gitDir + File.separatorChar + "gitpush.sh", "gitpush.sh",
					this.genAppOptions.getWorkingDir(), emptyObject);

			// ==============================================================
			// process the gitpush.xml
			// ==============================================================
			generateTheFileHelper(antDir + File.separatorChar + "gitpush.xml", "gitpush.xml",
					this.genAppOptions.getWorkingDir(), emptyObject);

			// ==============================================================
			// process the build.xml
			// ==============================================================
			generateTheFileHelper(antDir + File.separatorChar + "build.xml", "build.xml",
					this.genAppOptions.getWorkingDir(), emptyObject);

		}

		// ==============================================================
		// next handle the containers recursively,
		// starting at the provided working directory
		// ==============================================================
		componentsToHandle.clear();
		componentsToHandle.put(UMLRootType.CONTAINERS, modelParser.getContainers());

		// ==============================================================
		// next handle the subsystems and components recursively,
		// starting at the provided working directory
		// ==============================================================
		
		// ==============================================================
		// first, generate the files by parsing through the root of the package
		// path itself
		// ==============================================================
		this.overWriteFlag = true;
		this.setTheTemplatePathToUse(this.packageRootPath);
		generateTheFilesRecursively(this.genAppOptions.getWorkingDir(), componentsToHandle);

		this.overWriteFlag = true;
		this.setTheTemplatePathToUse(this.resourceOverridePath);
		generateTheFilesRecursively(this.genAppOptions.getWorkingDir(), componentsToHandle);

		// ==============================================================
		// next set the overwrite flag to false and parse through the common
		// package path
		// ==============================================================
		this.overWriteFlag = false;
		this.setTheTemplatePathToUse(this.commonTemplatesPath);
		generateTheFilesRecursively(this.genAppOptions.getWorkingDir(), componentsToHandle);

		
	}

	/**
	 * depending on the object type in the whatToHandle Map of Lists, figures out
	 * the new root directory relative to the input outputRootDir value, and forces
	 * a generation of the object itself and its children underneath
	 * 
	 * @param outputRootDir		the root of where to generate into
	 * @param whatToHandle		the map of UMLRootType/BaseModelObject
	 *                           collections to generate through
	 */
	protected void generateTheFilesRecursively(final String outputRootDir,
												final Map<UMLRootType, List<? extends BaseModelObject>> whatToHandle) {

		whatToHandle.entrySet().forEach(entry -> {
			final List<BaseModelObject> singleParentList 			= new ArrayList<>();
			final UMLRootType rootType 								= entry.getKey();
			final List<? extends BaseModelObject> collectionToUse 	= whatToHandle.get(rootType);

			// =========================================================
			// handle the containers subsystems and components basically the
			// same
			// =========================================================
			if (rootType == UMLRootType.CONTAINERS || rootType == UMLRootType.SUBSYSTEMS
					|| rootType == UMLRootType.COMPONENTS) {

				// ================================================================================
				// iterate thru each container, adjust the output path by
				// appending its name generate it, then recurse this method
				// through its contained
				// base model objects
				// ================================================================================
				collectionToUse.forEach(baseModelObject -> {
					final String modifiedOutputFileDir = outputRootDir + File.separator + "Container-"
							+ baseModelObject.getName();

					Map<UMLRootType, List<? extends BaseModelObject>> maps = new EnumMap<>(UMLRootType.class);

					// ================================================================================					
					// create the directory and the maven like output structure
					// ...just in case (adds a bit of gen time overhead)
					// ================================================================================
					createOutputDir(modifiedOutputFileDir);
					
					// ================================================================================
					// apply the current model object to the project generation context
					// ================================================================================
					applyBaseModelObjectToContext(rootType, baseModelObject);

					/*
					 * only apply the one parent container base model object being looped through
					 * and generate it separately
					 */
					singleParentList.add(baseModelObject);
					maps.put(rootType, singleParentList);
					try {
						// ================================================================================
						// process generating files into the output directory
						// ================================================================================						
						generateTheFiles(modifiedOutputFileDir, maps);
					} catch (Exception exc) {
						LOGGER.log(Level.SEVERE, GENERATE_APP_FAILURE, exc);
					}
					// ================================================================================
					// increment the count
					// ================================================================================
					generateAppStats.increment(rootType);
					
					// ================================================================================
					// clear the working Map
					// ================================================================================
					maps.clear();

					try {
						// ================================================================================
						// recursively generate the files
						// ================================================================================
						generateFilesForChildrenRecursively(baseModelObject, maps, rootType, modifiedOutputFileDir);
					} catch (Exception exc) {
						LOGGER.log(Level.SEVERE, GENERATE_APP_FAILURE, exc);
					}

					maps.clear();
					singleParentList.clear();
				});
			}
		});
	}

	/**
	 * Method to handle the generation of all files found under the assigned
	 * theTemplateRootPathToUse. Each file it discovers is treated as a template
	 * file and is run through the template engine. It uses the directory mappings
	 * contained in the MASTER_DIR_MAP_FILE to figure out where exactly to output
	 * the generated file to
	 * 
	 * @param outputRootDir - the root of where to generate into
	 * @param whatToHandle  - the map of UMLRootType/BaseModelObject collections to
	 *                      generate through
	 * @throws GenerationException
	 */
	protected void generateTheFiles(String outputRootDir,
									Map<UMLRootType, List<? extends BaseModelObject>> whatToHandle) 
											throws GenerationException {
		String outputFileDir = null;
		boolean recursive = true;
		String[] extensions = null;
		Iterator<java.io.File> templateFiles = FileUtils.iterateFiles(new java.io.File(this.getTheTemplatePathToUse()),
				extensions, recursive);

		if (templateFiles != null) {
			java.io.File templateFile = null;

			while (templateFiles.hasNext()) {

				templateFile = templateFiles.next();
				// ================================================================================
				// do nothing if the file we grabbed is a template file
				// ================================================================================
				if (templateFile.getName().endsWith(MACRO_FILE_EXTENSION))
					continue;

				// ================================================================================
				// figure out if there is a prefixed file separator
				// ================================================================================
				String determinedOutputPath = adjustPath(this.determineOutputPath(templateFile.getName()));

				// ================================================================================
				// 01-06-2021
				// Now can support output to multiple locations so need
				// to check if the outputPath contains more than one comma
				// delimited locations
				// ================================================================================
				StringTokenizer outputPathToken = new StringTokenizer(determinedOutputPath, ",");
				
				while (outputPathToken.hasMoreTokens()) {
					// ================================================================================
					// figure out the actual output of the file
					// ================================================================================
					outputFileDir = outputRootDir + outputPathToken.nextToken();

					// ================================================================================
					// generate the files using the template
					// ================================================================================
					generateTheFilesHelper(templateFile, outputFileDir, whatToHandle);
				}
			}
		}
	}

	/**
	 * Helper method to generate an individual templat file against a set of model objects
	 * 
	 * @param templateFile				the template file to generate against
	 * @param outputFileDir				the file name and location to generate into
	 * @param baseModelObjectMapping	the model objects to use
	 * @throws GenerationException		general generation exception
	 */
	protected void generateTheFilesHelper(	File templateFile, 
											String outputFileDir,
											Map<UMLRootType, List<? extends BaseModelObject>> baseModelObjectMapping) 
												throws GenerationException {
		// ================================================================================
		// The true point of this method is to determine if we have a template defined
		// in such
		// a way as to cause looping over the set of baseModelObjects, or do we have a
		// one off
		// that needs to be treated individually.
		//
		// situations that cause that those whereby __classes__, __enums__, etc.. are in
		// the prefix
		// or $className is part of the output path, which should be checked first
		// ================================================================================
		boolean foundMatch 				= false;
		UMLRootType umlRootType 		= null;
		String umlRootTypeAsStringKey 	= "";

		if (outputFileDir.contains(CLASS_NAME_INDICATOR)) {
			foundMatch = true;
			umlRootType = UMLRootType.CLASSES;
		} else {
			for (int index = 0; !foundMatch && index < UMLRootType.values().length; index++) {
				umlRootTypeAsStringKey = UMLRootType.values()[index].toString();

				if (templateFile.getName().indexOf(umlRootTypeAsStringKey) > -1) {
					foundMatch = true;
					umlRootType = UMLRootType.values()[index];
				}
			}
		}

		if (foundMatch) {
			// ================================================================================
			// need to see if we have something to handle it
			// ================================================================================
			List<? extends BaseModelObject> baseModelObjects = baseModelObjectMapping.get(umlRootType);

			if (baseModelObjects != null)
				generateTheFilesHelper(templateFile.getAbsolutePath(), templateFile.getName(), outputFileDir,
						umlRootType, baseModelObjects);
			else {
				final String msg = "No match found for " + umlRootType.toString() + " and template file "
						+ templateFile.getName();
				LOGGER.log(Level.INFO, msg);
			}
		}
		// ================================================================================
		// treat it as a loose file, not requiring multiple generations
		// ================================================================================
		else {
			handleLooseFileGen(outputFileDir, templateFile);
		}

	}

	/**
	 * Helper method to generate an individual template file location and name against a set of model objects
	 * 
	 * @param templateFile				the template file to generate against
	 * @param outputFileDir				the file name and location to generate into
	 * @param baseModelObjectMapping	the model objects to use
	 * @throws GenerationException		general generation exception
	 */
	protected void generateTheFilesHelper(String templateFileAbsolutePath, String templateFileName,
			String outputFileDir, UMLRootType rootType, List<? extends BaseModelObject> baseModelObjects)
			throws GenerationException {

		// ================================================================================
		// test for PARENT_ONLY label within file name, tag and replace
		// ================================================================================
		boolean parentOnly = templateFileName.contains(PARENT_ONLY);

		if (parentOnly)
			templateFileName = templateFileName.replace(PARENT_ONLY, "");

		for (BaseModelObject baseModelObject : baseModelObjects) {
			// ================================================================================
			// if the tag PARENT_ONLY does not exist OR the model object has no parenmt
			// ================================================================================			
			if (!parentOnly || !baseModelObject.hasParent()) {
				
				// ================================================================================
				// generate a file against a single model object
				// ================================================================================
				generateTheFileHelper(templateFileAbsolutePath, templateFileName, outputFileDir, baseModelObject);
				
				// ================================================================================
				// increment the counter for the type
				// ================================================================================
				this.generateAppStats.increment(rootType);
			}
		}
	}

	/**
	 * Helper method to generate an individual template file location and name against a model object 
     *
	 * @param templateFileAbsolutePath  the absoluate path of the template file
	 * @param templateFileName			the name of the template file
	 * @param outputFileDir             the directory to output the resulting generation to
	 * @param baseModelObject           the model object to consider
	 * @throws GenerationException      thrown during generation processing
	 */
	protected void generateTheFileHelper(String templateFileAbsolutePath, 
										String templateFileName, 
										String outputFileDir,
										BaseModelObject baseModelObject) 
		throws GenerationException {

        // -----------------------------------------------------------------		  
		// apply the basemodel object
        // -----------------------------------------------------------------		  		
		this.applyDataToContext(CLASS_OBJECT, baseModelObject);

        // -----------------------------------------------------------------		  
		// at this point, it is possible the outputFileDir contains a $className
		// directive, so replace
		// it with the baseModelObject's name
        // -----------------------------------------------------------------		  
		outputFileDir = outputFileDir.replace(CLASS_NAME_INDICATOR, baseModelObject.getName());

        // -----------------------------------------------------------------		  
		// safe to finally create the output file directory
        // -----------------------------------------------------------------		  
		createOutputDir(outputFileDir);

        // -----------------------------------------------------------------		  
		// need to peek and see if __roles__ is in use
        // -----------------------------------------------------------------		  		
		if (templateFileName.contains(UMLRootType.ROLES.name()) && baseModelObject instanceof ClassObject) {
	        // -----------------------------------------------------------------		  			
			// need to remove any prefix clue from the file name before proceeding
	        // -----------------------------------------------------------------		  			
			templateFileName = replacePrefixWithObjectName(templateFileName, baseModelObject.getName(),
					UMLRootType.CLASSES);

			for (AssociationEndObject association : ((ClassObject) baseModelObject).getAssociations()) {
				this.applyDataToContext("currentAssociation", association);
				templateFileName = replacePrefixWithObjectName(templateFileName, baseModelObject.getName(),
						UMLRootType.ROLES);
				this.mergeTemplate(templateFileAbsolutePath, outputFileDir, templateFileName);
			}
		} 
        // -----------------------------------------------------------------
		// do it without regard to associations
        // -----------------------------------------------------------------		  
		else  {
	        // -----------------------------------------------------------------		  			
			// need to remove any prefix clue from the file name before
			// proceeding
	        // -----------------------------------------------------------------		  			
			templateFileName = replacePrefixWithObjectName(templateFileName, baseModelObject.getName(), null);
			this.mergeTemplate(templateFileAbsolutePath, outputFileDir, templateFileName);
		}

	}

	/**
	 * General helper to handle generation of files not directly affiliated with a
	 * ClasssObject type
	 * 
	 * @param outputFileDir 		where to place the resulting generated file
	 * @param templateFile  		the absolute path to the template file
	 * @throws GenerationException  thrown during generation processing 
	 */
	protected void handleLooseFileGen(String outputFileDir, java.io.File templateFile) 
			throws GenerationException {
		try {
	        // -----------------------------------------------------------------		  						
			// safe to create the output file directory
	        // -----------------------------------------------------------------		  						
			createOutputDir(outputFileDir);

	        // -----------------------------------------------------------------
			// merge the template with the resulting destination/file
	        // -----------------------------------------------------------------		  			
			this.mergeTemplate(templateFile.getAbsolutePath(), outputFileDir, templateFile.getName());
		} catch (GenerationException exc) {
			LOGGER.log(Level.SEVERE, "Failed to handle loose file " + templateFile.getAbsolutePath(), exc);
			throw exc;
		}
	}

	/**
	 * Merges the template file with the actual file, and places the results in the
	 * outputDir
	 * 
	 * @param templateFileName	absolute path of the template file name
	 * @param outputDir        	where to dump the results to
	 * @param outputFile       	what to name the generated file
	 * @throws GenerationException
	 */
	public void mergeTemplate(String templateFileName, String outputDir, String outputFile) throws GenerationException {

        // -----------------------------------------------------------------		  			
		// some files are to be ignored and not treated as templates
        // -----------------------------------------------------------------		  			
		if (!checkToTreatFileAsATemplate(templateFileName, outputDir))
			return;

        // -----------------------------------------------------------------		  			
		// need the template file name portion relative to the root of the
		// package which is the root Velocity has been pointed to...+1 to eat the path
		// separator character
        // -----------------------------------------------------------------		  			
		templateFileName = templateFileName.substring(this.getTheTemplatePathToUse().length() + 1);

        // -----------------------------------------------------------------		  			
		// if not a discoverable resource by Velocity, not since in going on
        // -----------------------------------------------------------------		  			
		if (!velocityEngine.resourceExists(templateFileName)) {
			final String msg = "Velocity Template Engine cannot locate resource " + templateFileName;
			LOGGER.log(Level.SEVERE, msg);
			return;
		}

		Template template = null;

		try {
	        // -----------------------------------------------------------------		  			
			// Ask Velocity to return a Template given the template file name
	        // -----------------------------------------------------------------		  			
			template = velocityEngine.getTemplate(templateFileName);
			
	        // -----------------------------------------------------------------		  			
			// increment the counter based on the extension
	        // -----------------------------------------------------------------		  			
			generateAppStats.increment(templateFileName);

		} catch (ResourceNotFoundException resourcenotfoundexception) {
			LOGGER.warning("resource not found Exception for " + templateFileName);
		} catch (ParseErrorException parseerrorexception) {
	        // -----------------------------------------------------------------		  			
			// still copy the file but skip the code below which handles the
			// actual template and merge processing
	        // -----------------------------------------------------------------		  						
			LOGGER.severe("parseerrorexception encountered - copied " + this.getTheTemplatePathToUse() + File.separator
					+ templateFileName + " to " + outputDir + File.separator + templateFileName);

	        // -----------------------------------------------------------------		  			
			// source and destination of copy
	        // -----------------------------------------------------------------		  			
			File srcFile = new File(this.getTheTemplatePathToUse() + File.separator + templateFileName);
			File destFile = new File(outputDir);

			try {
		        // -----------------------------------------------------------------		  			
				// handle copying to the destination as is
		        // -----------------------------------------------------------------		  			
				FileUtils.copyFileToDirectory(srcFile, destFile);
			} catch (IOException ioExc) {
				LOGGER.warning("failed to copy " + srcFile + " to " + destFile);
			}
		} catch (MethodInvocationException methodinvocationexception) {
			LOGGER.severe("methodinvocationexception for " + templateFileName);
		} catch (Exception exception) {
			LOGGER.severe("general Exception for " + templateFileName);
		}

        // -----------------------------------------------------------------		  			
		// if here, things are sound and ready to do a template merger
        // -----------------------------------------------------------------		  			

		// -----------------------------------------------------------------		  			
		// prepare the output file
        // -----------------------------------------------------------------		  			
		File preparedFile = this.prepareOutputFile(outputDir, outputFile);

		try ( FileWriterWithEncoding writer = new FileWriterWithEncoding(preparedFile, "utf-8") ){
			if (template != null) {
				template.merge(context, writer);
			}
		} catch (Exception exc) {
			LOGGER.warning(
					"Failed to write template " + outputFile + " to " + outputDir + " : " + exc.getLocalizedMessage());
		} finally {
			try {
				// -----------------------------------------------------------------		  			
				// increment line count
				// -----------------------------------------------------------------		  							
				generateAppStats.incrementLineCount(countLines(outputDir + File.separator + outputFile, null));
			} catch (IOException ioExc) {
				LOGGER.log(Level.WARNING, "GenerateApp.mergeTemplate()", ioExc);
			}
		}
	}

	/**
	 * Helper method use to map the provided outputFileName to a value found in the
	 * directory mappings property file. This method is an essential since it uses
	 * an algorithm implied and documented in the mappings property file.
	 * 
	 * @param 	outputFileName	the name of the file of interest
	 * @return 	the relative directory path to generate the provided outputFileName into
	 *         	
	 */
	protected String determineOutputPath(final String outputFileName) {
		String path 	= null;
		boolean foundIt = false;

		if (outputFileName != null) {
			
			// -----------------------------------------------------------------		  			
			// first check that the file itself is a mapping
			// -----------------------------------------------------------------		  			
			path = this.dirMappings.getProperty(outputFileName);
			if (path == null) { 
				// -----------------------------------------------------------------		  			
				// means the outputFileName itself is not a key
				// -----------------------------------------------------------------		  							
				Optional<Object> foundKey = dirMappings.keySet().stream()
						.filter(key -> testMappingKey(key, outputFileName)).findFirst();

				// -----------------------------------------------------------------
				// if the key exists, need to adjust the directory notation found
				// in a mapping.properties into an actual file system path
				// -----------------------------------------------------------------		  			
				if (foundKey.isPresent())
					return (adjustPathPackageNameAndAppName(dirMappings.getProperty(foundKey.get().toString())));
			} else {
				// -----------------------------------------------------------------		  			
				// the output file itself was a key in the directory mapping
				// -----------------------------------------------------------------		  			
				foundIt = true;
			}

			// -----------------------------------------------------------------
			// adjust the directory notation into an actual file system path
			// -----------------------------------------------------------------		  			
			if (foundIt) {
				path = adjustPathPackageNameAndAppName(path);
			} else {
				// -----------------------------------------------------------------		  			
				// return the code generation catch all directory
				// -----------------------------------------------------------------		  			
				path = CODE_GEN_CATCH_ALL_DIR;
			}
		}

		return (path);
	}

	/**
	 * helper used to adjust a path that uses .notation with the correct file system
	 * specific separator. The method then replaces the phrase $` with the actual
	 * package name and $className that is assigned as as an application option
	 * (application.package name). *
	 * 
	 * @param path 	path to adjust
	 * @return 		adjusted path
	 */
	protected String adjustPathPackageNameAndAppName(String path) {
		// -----------------------------------------------------------------		  			
		// we use .notation in the properties file so we can apply system
		// specific dir separators here, however a '..' means the directory begins
		// with a '.', which is the case for CircleCI and others
		// -----------------------------------------------------------------		  			

		path = path.replace(".", java.io.File.separator);
		path = path.replace(java.io.File.separator + java.io.File.separator, java.io.File.separator + ".");

		if (path.contains(PACKAGE_NAME_INDICATOR)) {
			// -----------------------------------------------------------------		  			
			// handles . swap with / character
			// -----------------------------------------------------------------		  						
			String pkg = this.genAppHelper.getRootPackageAsDirectory(); 

			// -----------------------------------------------------------------		  						
			// use this one if none provided
			// -----------------------------------------------------------------		  									
			if (pkg == null)
				pkg = "com/company"; 

			// -----------------------------------------------------------------		  									
			// replace it with the actual path name of the package
			// -----------------------------------------------------------------		  									
			path = path.replace(PACKAGE_NAME_INDICATOR, pkg);
		}

		// -----------------------------------------------------------------		  						
		// handle the application name replacement next
		// -----------------------------------------------------------------		  								
		path = adjustPathAppName(path);

		return (path);
	}

	/**
	 * helper used to adjust a path that uses .notation with the correct file system
	 * specific separator. The method then replaces the phrase $appName with the
	 * actual package name that is assigned as as an application option
	 * (application.package name).
	 * 
	 * @param path	path to adjust
	 * @return 		adjusted path
	 */
	protected String adjustPathAppName(String path) {

		if (path.contains(APP_NAME_INDICATOR)) {
			String app = this.genAppHelper.getApplicationNameFormatted();

			if (app == null)
				app = "";

			// -----------------------------------------------------------------		  						
			// replace it with the actual path name itself of the package
			// -----------------------------------------------------------------		  									
			path = path.replace(APP_NAME_INDICATOR, app);
		}

		return (path);
	}

	/**
	 * internal method used to create an output File labeled by the given
	 * path/fileName
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 * @throws GenerationException
	 */
	protected File prepareOutputFile(String path, String fileName) throws GenerationException {
		if (fileName == null)
			throw new IllegalArgumentException("GenerateApp::createPrintWriter(..) - fileName cannot be null.");

		File outputFile = new File(path, fileName);

		// -----------------------------------------------------------------		  						
		// get out if the overWriteFlag == false and the output file exists
		// -----------------------------------------------------------------		  								
		if (!this.overWriteFlag && outputFile.exists()) {
			return (null);
		}
		File directoryFile = new File(path);
		directoryFile.mkdir();

		try {
			if (!outputFile.createNewFile()) {
				final String msg = "Failed to create file for " + outputFile;
				LOGGER.log(Level.WARNING, msg);
			}
		} catch (Exception exc) {
			throw new GenerationException(
					"GenerateApp::createPrintWriter(..) - Could not create output file because: " + exc);
		}

		return outputFile;
	}

	/**
	 * Starting at the rootDir, iterates through it sub-directory loading absolute
	 * path location of any macros.vm files it finds.
	 * 
	 * @param 	rootDir absolute file path to start iterating through
	 * @return 			a comma separated listing of discovered absolute file paths of each
	 *         			discovered macros.vm files
	 */
	protected String getMacroFiles(String rootDir) {
		boolean recursive 				= true;
		String[] extensions 			= { VM_EXTENSION };
		StringBuilder macros 			= new StringBuilder();
		Iterator<java.io.File> files 	= FileUtils.iterateFiles(new java.io.File(rootDir), extensions, recursive);

		if (files != null) {
			String modPath 		= null;
			java.io.File file	= null;

			// -----------------------------------------------------------------		  						
			// iterator over all files with a vm extension
			// -----------------------------------------------------------------		  								
			while (files.hasNext()) {
				file = files.next();
				
				// -----------------------------------------------------------------
				// need to remove the root part from the path itself...+1 to
				// eat the path separator char
				// -----------------------------------------------------------------				
				modPath = file.getAbsolutePath();
				modPath = modPath.substring(rootDir.length() + 1);
				
				macros.append(modPath);
				macros.append(",");
			}
		}

		return (macros.toString());
	}

	/**
	 * internal helper method used to establish the setting up of the Velocity
	 * Engine
	 * 
	 * @return Properties	the mapping properties loaded
	 */
	protected Properties handleAssigningProperties() {
		Properties p = new Properties();
		String cache = System.getProperty("VELOCITY_CACHE");
		String macrosInDirStructure;

		// -----------------------------------------------------------------
		// get the macro files from the common templates package and
		// the technology stack package in use
		// -----------------------------------------------------------------
		macrosInDirStructure = getMacroFiles(commonTemplatesPath) + getMacroFiles(packageRootPath);

		p.setProperty("file.resource.loader.cache", cache != null ? cache : "false");
		p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		p.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
		p.setProperty("file.resource.loader.modificationCheckInterval", "2");

		// -----------------------------------------------------------------
		// the order is important for the sake of being discovered
		// -----------------------------------------------------------------		
		p.setProperty("file.resource.loader.path",  this.resourceOverridePath + "," + this.packageRootPath + ", " + this.commonTemplatesPath );

		p.setProperty("resource.loader", "file");
		p.setProperty("runtime.log", "velocity_realmethods.log");

		p.setProperty("velocimacro.library", macrosInDirStructure);
		p.setProperty("velocimacro.library.autoreload", "true");

		return (p);
	}

	/**
	 * load the key/value pairings of the directory mapping file, into dirMappings.
	 * Also, combine the mappings from the common template path and only add those
	 * that do not exist in the mapping file of the template
	 * 
	 * @throws	IOException		thrown in the event of an I/O issue
	 */
	protected void loadDirMappings() throws IOException {
		// -----------------------------------------------------------------
		// load the technology stack package mappings
		// -----------------------------------------------------------------
		dirMappings = loadDirMappingsHelper(this.packageRootPath);		
		LOGGER.log(Level.INFO, "Package Mappings are : {0}", this.dirMappings);
		this.overWriteFlag = false;
		
		// -----------------------------------------------------------------
		// load the common template mappings
		// -----------------------------------------------------------------
		Properties commonProperties = loadDirMappingsHelper(this.commonTemplatesPath);
		LOGGER.log(Level.INFO, "Commong Mappings are : {0}", commonProperties);

		// -----------------------------------------------------------------
		// combine the template mappings with the mappings from 
		// the technology stack package
		// -----------------------------------------------------------------
		dirMappings.putAll(commonProperties);

		// -----------------------------------------------------------------
		// handle mapping exclusions
		// -----------------------------------------------------------------
		handleMappingExclusions();

		LOGGER.log(Level.INFO, "Final File Mappings are : {0}", this.dirMappings);
	}

	/**
	 * Helper method to load the mappings.properties content from a given path.
	 * Since a mapping.properties itself can contain template directives, it
	 * is run through the template processing before returning the contents.
	 * 
	 * @param mappingPath	the path to look for a mappings.properties file 
	 * @return				the loaded properties
	 * @throws IOException  thrown in the even of an I/O issue
	 */
	protected Properties loadDirMappingsHelper(String mappingPath) throws IOException {

		String mappingFileAsTemplate 	= mappingPath + File.separator + MASTER_DIR_MAP_FILE;
		String tempDirPath 				= org.apache.commons.io.FileUtils.getTempDirectoryPath();
		String tempMappingFile 			= org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(16) + "_"
											+ MASTER_DIR_MAP_FILE;
		Properties localDirMappings 	= new Properties();

		LOGGER.log(Level.INFO, "***Mapping path is {0}", mappingPath);

		// -----------------------------------------------------------------		
		// this establishes the root of the templates
		// -----------------------------------------------------------------		
		this.setTheTemplatePathToUse(mappingPath);

		// -----------------------------------------------------------------
		// lets run it through the template handler to see if it needs processing
		// -----------------------------------------------------------------
		try {
			mergeTemplate(mappingFileAsTemplate, tempDirPath, tempMappingFile);
			processedMapFilePath = tempDirPath + File.separator + tempMappingFile;
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Failure during processing mappings file " + mappingPath
					+ " as a template.  Will use it unprocessed.", exc);
			processedMapFilePath = mappingFileAsTemplate;
		}

		LOGGER.log(Level.INFO, "Processed Mapping path is {0}", processedMapFilePath);

		// ----------------------------------------------------------------
		// load the properties into the local Properties object
		// -----------------------------------------------------------------		
		try (java.io.InputStream input = new java.io.FileInputStream(processedMapFilePath)) {
			localDirMappings.load(input);
		} catch (FileNotFoundException fnExc) {
			LOGGER.log(Level.WARNING, "Failure during mappings file loading of " + processedMapFilePath, fnExc);
		} 

		// -----------------------------------------------------------------		
		// delete the temporary processedMapFilePath
		// -----------------------------------------------------------------				
		deleteQuietly(processedMapFilePath);

		return localDirMappings;
	}

	/**
	 * handles locating the file exclusions key from the dirMappings and stores each
	 * exclusion as an entry in a HashSet
	 */
	private void handleMappingExclusions() {
		// -----------------------------------------------------------------
		// locate the exclusions from the mappings.properties and store
		// in the exclusions Map
		// -----------------------------------------------------------------		
		Object exclusionValues = dirMappings.get(EXCLUSIONS_KEY);
		if (exclusionValues != null) {
			// -----------------------------------------------------------------		
			// the exclusions are tokenized as a comma delimited String
			// -----------------------------------------------------------------		
			StringTokenizer tokenizer = new StringTokenizer(exclusionValues.toString(), ",");
			while (tokenizer.hasMoreTokens()) {
				exclusions.add(tokenizer.nextToken());
			}
			LOGGER.log(Level.INFO, "Loaded exclusions as : {0}", exclusions);
			
			// -----------------------------------------------------------------		
			// safe to remove the exclusions key
			// -----------------------------------------------------------------					
			dirMappings.remove(EXCLUSIONS_KEY);
		}
	}

	/**
	 * helper method used to create the provided directory and any sub-directories
	 * required to actual create
	 * 
	 * @param outputDir the directory to create
	 */
	private void createOutputDir(String outputDir) {
		try {
			if (!new File(outputDir).exists()) {
				java.nio.file.Path outputPath = java.nio.file.Paths.get(outputDir);
				java.nio.file.Files.createDirectories(outputPath);
			}
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "GenerateApp.createOutputDir()", exc);
		}
	}

	/**
	 * Force the path to start with the correct java.io.File.separator value
	 * 
	 * @param path
	 * @return String
	 */
	private String adjustPath(String path) {
		if (!path.startsWith("/") && !path.startsWith("\\"))
			path = java.io.File.separator + path;

		return path;
	}

	/**
	 * Helper method to replace the indicator tag of a file with the provided object
	 * name.
	 * 
	 * For instance, file __classes__.java with object name Account would be account
	 * Account.java.
	 * 
	 * @param fileName		the original file name to work on
	 * @param objectName	the name of the object referenced
	 * @param type          the type of the current model object being processed
	 * @return              the new name of the modified file
	 */
	private String replacePrefixWithObjectName(String fileName, String objectName, UMLRootType type) {

		if (objectName != null && objectName.length() > 0) {
			if (type == null) {
				for (UMLRootType prefix : UMLRootType.values()) {
					if (fileName.contains(prefix.toString())) {
						fileName = fileName.replace(prefix.toString(), objectName);
						return (fileName);
					}
				}
			} else {
				fileName = fileName.replace(type.toString(), objectName);
			}
		}
		return (fileName);
	}

	/**
	 * Test method to determine if the provided list is null.
	 * 
	 * @param rootType	model object type
	 * @param list      the list to check null against
	 * @return 			the list itself for operator convenience
	 */
	private List<? extends BaseModelObject> assertNotNull(UMLRootType rootType, List<? extends BaseModelObject> list) {
		if (list == null) {
			final String msg = "List to test is NULL for UMLRootType " + rootType.toString();
			LOGGER.log(Level.INFO, msg);
		}

		return (list);
	}

	/**
	 * Checks to see if the file itself is in the directory mapping.
	 * 
	 * If not found, checks to see if the file extension is in the exlcusion list.
	 * 
	 * Move this list of exclusions into the mapping.properties itself
	 * 
	 * @param fileName	file name to process
	 * @return
	 */
	private boolean validFileToProcess(String fileName) {

		// -----------------------------------------------------------------				
		// if the file is in the exclusion list, is it not considered valid to process
		// -----------------------------------------------------------------		
		if (excludeFile(fileName))
			return false;

		// -----------------------------------------------------------------		
		// if the file itself is explicitly included in the mapping, it is
		// valid, regardless it's type
		// -----------------------------------------------------------------		
		try {
			String name = FilenameUtils.getName(fileName);
			if (name != null && !name.isEmpty() && this.dirMappings.containsKey(name))
				return true;
		} catch (Exception ioExc) {
			// -----------------------------------------------------------------		
			// something invalid about the file so return false
			// -----------------------------------------------------------------					
			LOGGER.warning("valid file to process check on " + fileName + " turned up " + ioExc);
			return (false);
		}

		// -----------------------------------------------------------------		
		// these files are explicity ignored for processing
		// -----------------------------------------------------------------		
		String extension 			= fileName.substring(fileName.length() - 4);
		String[] excludeExtensions 	= { ".png", ".gif", ".jpg", ".jpeg", ".zip", ".jar", ".tar", ".war", ".ear",
				".exe" };

		for (String ext : excludeExtensions) {
			if (ext.equalsIgnoreCase(extension))
				return (false);
		}

		// -----------------------------------------------------------------		
		// treat javascript files different because we only want to process
		// when specified, otherwise ignore since so much of its syntax looks
		// like Velocity template syntax
		// -----------------------------------------------------------------				
		extension = fileName.substring(fileName.length() - 3);
		String[] excludeExtensions2 = { ".js" };
		for (String ext : excludeExtensions2) {
			if (ext.equalsIgnoreCase(extension) && fileName.indexOf(CLASSES_INDICATOR) < 0
					&& fileName.indexOf(CLASS_NAME_INDICATOR) < 0)
				return (false);
		}

		return (true);
	}

	/**
	 * Checks exclusion list to determine if the file should be excluded
	 * 
	 * @param	fileName	the name of the file to check if it should be included
	 * @return	true/false  indicator on exclusion text
	 * 
	 */
	private boolean excludeFile(String fileName) {
		for (String exclusion : exclusions) {
			// -----------------------------------------------------------------
			// test if the fileName is part of the exclusion Map
			// -----------------------------------------------------------------		
			if (testMappingKey(exclusion, FilenameUtils.getName(fileName))) {
				LOGGER.log(Level.INFO, () -> "Exclusion found as " + exclusion + " : " + fileName);
				return true; // exclude it
			}
		}

		// -----------------------------------------------------------------		
		// do not exclude
		// -----------------------------------------------------------------				
		return (false);
	}

	/**
	 * Counts the number of lines for a given file.
	 * 
	 * @param filePath		path of file
	 * @param encoding      encoding method to apply
	 * @return              number of lines counted
	 * @throws IOException
	 */
	protected long countLines(String filePath, String encoding) throws IOException {
		long lines 					= 0;
		File file 					= new File(filePath);
		LineIterator lineIterator 	= FileUtils.lineIterator(file, encoding);
		
		while (lineIterator.hasNext()) {
			lines++;
			lineIterator.nextLine();
		}
		return lines;
	}

	/**
	 * Returns the theTemplatePathToUse field.
	 * 
	 * @return String   the template path inuse
	 */
	private String getTheTemplatePathToUse() {
		return this.theTemplatePathToUse;
	}

	/**
	 * Assigns the theTemplatePathToUse field the provided argument.
	 * 
	 * @param path	the template path in use
	 */
	private void setTheTemplatePathToUse(String path) {
		this.theTemplatePathToUse = path;
	}

	/**
	 * Determines if the fileName is a template or should be treated as a normal
	 * file.
	 * 
	 * If a normal file, it will copy it to the outputDir
	 * 
	 * @param 	templateFileName	template file to consider
	 * @param	outputDir			the locatino where to output 
	 * @return
	 */
	protected boolean checkToTreatFileAsATemplate(String fileName, String outputDir) {
		// -----------------------------------------------------------------				
		// do a simple copy if the file shouldn't be treated as a template
		// -----------------------------------------------------------------						
		if (!validFileToProcess(fileName)) {
			fileName = fileName.substring(this.getTheTemplatePathToUse().length() + 1);

			File srcFile = new File(this.getTheTemplatePathToUse() + File.separator + fileName);
			File destFile = new File(outputDir);

			try {
				// -----------------------------------------------------------------								
				// simple copy
				// -----------------------------------------------------------------								
				FileUtils.copyFileToDirectory(srcFile, destFile);
			} catch (IOException ioExc) {
				LOGGER.log(Level.WARNING, "Invalid file type found...FAILED To Copy " + srcFile + " to " + "destFile.",
						ioExc);
			}

			return false;
		}

		return true;
	}

	/**
	 * Helper method to determine if the mapping itself contains 0 to 1 asterisk. If
	 * so, it will evaluate the mapping to will treat the mapping with a certain
	 * wild card flavor and compare the outputFileName to it.
	 * 
	 * @param firstStarIndex	first star (asterisk) index location
	 * @param key				the object key
	 * @param mapping			the object mapping
	 * @param outputFileName    the output file name
	 * @return
	 */
	private boolean evaluateWhenOnlyOneAsterisk(int firstStarIndex, Object key, String mapping, String outputFileName) {
		boolean foundIt = false;
		String prefix = null;
		String suffix = null;

		// -----------------------------------------------------------------
		// at the beginning		
		// -----------------------------------------------------------------				
		if (firstStarIndex == 0) {
			if (outputFileName.endsWith(mapping)) {
				foundIt = true;
			}
		} 
		// -----------------------------------------------------------------
		// at the end, zero based		
		// -----------------------------------------------------------------						
		else if (firstStarIndex == key.toString().length() - 1) {
			if (outputFileName.startsWith(mapping)) {
				foundIt = true;
			}
		}
		// -----------------------------------------------------------------
		// must be in the middle
		// -----------------------------------------------------------------								
		else if (firstStarIndex != -1)  {
			// -----------------------------------------------------------------								
			// everything upt to the first asterisk is the prefix
			// -----------------------------------------------------------------								
			prefix = mapping.substring(0, firstStarIndex);
			// -----------------------------------------------------------------								
			// everything after the the first asterisk is the suffix
			// -----------------------------------------------------------------								
			suffix = mapping.substring(firstStarIndex);

			// -----------------------------------------------------------------
			// if the output file name contains the prefix and ends with 
			// the suffix, it has been found
			// -----------------------------------------------------------------								
			if (outputFileName.contains(prefix) && outputFileName.endsWith(suffix)) {
				foundIt = true;
			}
		}

		return (foundIt);
	}

	/**
	 * Helper method to determine if the mapping itself contains more than 1
	 * asterisk. If so, it will evaluate the mapping to will treat the mapping with
	 * a certain wild card flavor and compare the outputFileName to it.
	 * 
	 * @param firstStarIndex	index of the first star (asterisk)
	 * @param lastStarIndex     index of the last star (asterisk)
	 * @param mapping           the mapping from the mappings.properties file
	 * @param outputFileName    name of the output file
	 * @return                  true/false if found
	 */
	private boolean evaluateWhenMoreThanOneAsterisk(int firstStarIndex, int lastStarIndex, String mapping,
			String outputFileName) {

		boolean foundIt = false;
		String prefix = null;
		String suffix = null;
		String middle = null;

		// -----------------------------------------------------------------								
		// test for pattern *Component*.java, where 
		// middle is *Component* and suffix is *.java
		// -----------------------------------------------------------------								
		if (firstStarIndex == 0) {
			suffix = mapping.substring(lastStarIndex + 1);
			middle = mapping.substring(firstStarIndex + 1, lastStarIndex);

			if (outputFileName.endsWith(suffix)
					&& outputFileName.substring(0, outputFileName.lastIndexOf(suffix)).contains(middle)) {
				foundIt = true;
			}
		} 
		// -----------------------------------------------------------------								
		// Assume remaining pattern is Service*Component*.java
		// -----------------------------------------------------------------								
		else {
			prefix = mapping.substring(0, firstStarIndex - 1);
			suffix = mapping.substring(lastStarIndex);
			middle = mapping.substring(firstStarIndex, lastStarIndex - 1);

			if (outputFileName.startsWith(prefix) && outputFileName.endsWith(suffix)
					&& outputFileName.substring(firstStarIndex, lastStarIndex).contains(middle)) {
				foundIt = true;
			}
		}
		return (foundIt);
	}

	/**
	 * Helper method to determine if the key is found within the context of the
	 * outputFileName. It handles straight comparison as well as wild card
	 * inspection.
	 * 
	 * Internally delegates to evaluateWhenOnlyOneAsterisk or
	 * evaluateWhenMoreThanOneAsterisk
	 * 
	 * @param key
	 * @param outputFileName
	 * @return
	 */
	private boolean testMappingKey(Object mapKey, String outputFileName) {

		String mapping 			= mapKey.toString();
		boolean passed 			= false;
		int firstStarIndex 		= mapping.indexOf(ASTERISK);
		int lastStarIndex 		= mapping.lastIndexOf(ASTERISK);

		// -----------------------------------------------------------------
		// found 0 or 1 asterisks
		// -----------------------------------------------------------------
		if (firstStarIndex == lastStarIndex) {
			// -----------------------------------------------------------------			
			// clear out the asterisk
			// -----------------------------------------------------------------			
			mapping = mapping.replace(ASTERISK, "");
			
			// -----------------------------------------------------------------
			// evaluate when only one asterisk is part of the pattern
			// -----------------------------------------------------------------
			passed = evaluateWhenOnlyOneAsterisk(firstStarIndex, mapKey, mapping, outputFileName);
		} 
		// -----------------------------------------------------------------
        // evaluate when more than one asterisk is part of the pattern	
		// -----------------------------------------------------------------
		else {
			passed = evaluateWhenMoreThanOneAsterisk(firstStarIndex, lastStarIndex, mapping, outputFileName);
		}
		
		return passed;
	}

	/**
	 * Helper method to inspect the type and apply the baseModelObject to the
	 * template engine context.
	 * 
	 * @param type				model object type
	 * @param baseModelObject	the model object being processed
	 */
	private void applyBaseModelObjectToContext(UMLRootType type, BaseModelObject baseModelObject) {
		// -----------------------------------------------------------------
		// apply this to the context
		// -----------------------------------------------------------------
		switch (type) {
			case CONTAINERS:
				applyDataToContext("containerObject", baseModelObject);
				break;
			case SUBSYSTEMS:
				applyDataToContext(CLASS_OBJECT, baseModelObject);
				break;
			default:
				applyDataToContext(CLASS_OBJECT, baseModelObject);
				break;
		}
	}

	/**
	 * Internal helper to handle generating files by recursing through the hierarchy
	 * with the baseModelObject as its root. It discovers its children and invokes
	 * likewise and so on.
	 * 
	 * @param baseModelObject		the current model object 
	 * @param maps                  mapping of child objects to the current model object
	 * @param rootType              uml root type
	 * @param modifiedOutputFileDir where to output the generated file to
	 */
	private void generateFilesForChildrenRecursively(BaseModelObject baseModelObject,
			Map<UMLRootType, List<? extends BaseModelObject>> maps, UMLRootType rootType, String modifiedOutputFileDir)
			throws GenerationException {
		// -----------------------------------------------------------------
		// apply the children to the map and recurse through the them
		// -----------------------------------------------------------------
		switch (rootType) {
			case SUBSYSTEMS:
				maps.put(UMLRootType.SUBSYSTEMS, ((SubsystemObject) baseModelObject).getChildrenSubsystemObjects());
				maps.put(UMLRootType.COMPONENTS, ((SubsystemObject) baseModelObject).getChildrenComponentObjects());
				maps.put(UMLRootType.CLASSES, ((SubsystemObject) baseModelObject).getChildrenClassObjects());
				try {
					generateTheFilesRecursively(modifiedOutputFileDir, maps);
				} catch (Exception exc) {
					throw new GenerationException("Generate App Exeception", exc);
				}
				break;
			case CONTAINERS:
				maps.put(UMLRootType.CLASSES, ((ContainerObject) baseModelObject).getChildrenClassObjects());
				maps.put(UMLRootType.ENUMS, ((ContainerObject) baseModelObject).getChildrenEnumClassObjects());
				maps.put(UMLRootType.SERVICES, ((ContainerObject) baseModelObject).getChildrenServiceObjects());
				maps.put(UMLRootType.DTOS, ((ContainerObject) baseModelObject).getChildrenServiceObjects());
				try {
					generateTheFiles(modifiedOutputFileDir, maps);
				} catch (Exception exc) {
					throw new GenerationException("File Generation Exception", exc);
				}
				break;
			default:
				break;
		}

	}

	/**
	 * On the fly, pull down the common templates to apply to this application
	 * generation session
	 * 
	 * @return String
	 */
	private String determineCommonTemplates() {
		try {
			// -----------------------------------------------------------------
			// download and de-compress the common template package into a local directory
			// -----------------------------------------------------------------
			String tmpCommonTemplatePath 	= AppGenHelper.remedyFileLocation(AWSHelper.self().getS3BucketLocation() + AWSHelper.COMMON_TEMPLATES_FILE, "common_template_");
			String unzipDir 				= AppGenHelper.unzip(tmpCommonTemplatePath);

			// -----------------------------------------------------------------
			// delete the common template package file itself
			// -----------------------------------------------------------------
			deleteQuietly(org.apache.commons.io.FilenameUtils.getFullPath(tmpCommonTemplatePath));
			LOGGER.log(Level.INFO, "Common templates file successfully downloaded and unzipped from S3 bucket into {0}",
					unzipDir);
			return (unzipDir);
		} catch (ProcessingException exc) {
			// -----------------------------------------------------------------
			// return this as back-up, but the results will not be predictable
			// -----------------------------------------------------------------
			LOGGER.severe("Failed to locate common templates from S3 Bucket.  Using default installed templates.");
			return serverRootPath.replaceFirst("tech.stack.packages", "") + COMMON_TEMPLATES;
		}
	}

	/**
	 * helper method to handle apply resource references to the project generation session
	 */
	private void handleApplyingResources() {

		Map<String, String> options = this.genAppOptions.getOptions();
		
		try {
			// -----------------------------------------------------------------
			// create a resource override path.  the resources here will over ride
			// any resources that are output during a project generatin cycle.
	        // this is because a user's resource declaration/inclusion take precedent
			// -----------------------------------------------------------------
			resourceOverridePath = AppGenHelper.createTempWorkspaceDirectory("resource_");
			final String msg = "resourceOverridePath is " + resourceOverridePath; 
			LOGGER.info( msg );
		} catch( java.io.IOException ioExc ) {
			LOGGER.log(Level.WARNING, "failure during handling creating a temp directory", ioExc);
			return;
		}
		
		// -----------------------------------------------------------------
		// discover options with a suffice of .useResources
		// -----------------------------------------------------------------
		String resources 	= options.get( RESOURCES_AVAILABLE );
		final String msg	= "Resources are: " + resources; 
		LOGGER.info( msg );
		
		if( resources != null) {
			String resourceKey;
			StringTokenizer tokenizer = new StringTokenizer( resources );
			while ( tokenizer.hasMoreTokens() ) {
				resourceKey = tokenizer.nextToken();
				try {
					// ----------------------------------------------------------------					
					// locate the resource by the key
					// -----------------------------------------------------------------					
					Resource resource = (Resource)new ResourceDAO().findByName("Resource", resourceKey);
					
					// -----------------------------------------------------------------
					// if found and it is either public or private and owned by this user
					// -----------------------------------------------------------------
					if	( resource != null  &&
							( resource.getScopeType() == ScopeType.PUBLIC
								|| (resource.getOwnerId().compareTo(this.user.getId()) == 0) 
							) 
						) {
						// -----------------------------------------------------------------
						// the Url of the S3 object
						// -----------------------------------------------------------------
						String remoteResourceFilePath = AWSHelper.self().getS3BucketLocation() + resource.getFilePath();
						
						// -----------------------------------------------------------------
						// handles turning remote S3 object to a local file
						// -----------------------------------------------------------------
						File localResourceFilePath 	= new File(AppGenHelper.remedyFileLocation( remoteResourceFilePath, "resource_" ));
						
						// -----------------------------------------------------------------
						// figures out where to output the file according to how it is mapped 
						// within the technology stack package
						// -----------------------------------------------------------------
						LOGGER.log( Level.INFO, "localResourceFilePath is {0} " + localResourceFilePath.getAbsolutePath() );
						
						// -----------------------------------------------------------------
						// copy the file to the resourceOverridePath
						// -----------------------------------------------------------------
						FileUtils.copyFileToDirectory( localResourceFilePath, new File(resourceOverridePath) );
						
						// -----------------------------------------------------------------
						// delete the localResourceFilePath
						// -----------------------------------------------------------------
						deleteQuietly(org.apache.commons.io.FilenameUtils.getFullPath(localResourceFilePath.getAbsolutePath()));
					}
				} catch( Exception ex ) {
					LOGGER.log(Level.WARNING, "failure during handling applying resources", ex);
				}
			}
		}
			
	}
	
	/**
	 * helper method to quietly delete a file
	 * @param filePath	file to delete
	 */
	private void deleteQuietly( String filePath ) {
		if (org.apache.commons.io.FileUtils.deleteQuietly(new java.io.File(filePath)))
			LOGGER.log(Level.INFO, "Successfully deleted {0}", filePath);
		else
			LOGGER.log(Level.INFO, "Failed to delete {0}", filePath);
	}

    /**
	 * Helper to determine if the package is derived and if so, 
	 * merges the package and parent pacakge content into a temp working dir
     * @param		frameworkPackage	reference tech stack package
	 * @return		the determined package root path
	 */
	private String determinePackageRootPath( FrameworkPackage frameworkPackage ) {
		String determinedPath 		= frameworkPackage.getFilePath();
		final String derivedFrom 	= frameworkPackage.getDerivedFrom();

		try {
			// ====================================================================
			// reference the path where the package has been unzipped to unless
			// it is derived from another package, in which case merge them into
			// a temp directory
			// ====================================================================
			if ( derivedFrom != null && !derivedFrom.isEmpty() ) {
				FrameworkPackage parentPackage 	= new FrameworkPackageDAO().findByNameorId( derivedFrom );
				String tempTechStackPkgPath		= AppGenHelper.createTempWorkspaceDirectory("package_");

				// ====================================================================
				// copy the parent tech stack package content to the temp dir
				// ====================================================================
				FileUtils.copyDirectory( new File(parentPackage.getFilePath()), new File(tempTechStackPkgPath) );

				// ====================================================================
				// copy the target tech stack package content to the temp dir
				// ====================================================================
				FileUtils.copyDirectory( new File(frameworkPackage.getFilePath()), new File(tempTechStackPkgPath) );

				// ====================================================================
				// assign the temp dir as the package root path to use
				// ====================================================================
				determinedPath = tempTechStackPkgPath;

				final String msg = "Tech stack package " 
									+ frameworkPackage.getName() 
									+ " is derived From " + derivedFrom 
									+ " with combined working directory now " + tempTechStackPkgPath;
				LOGGER.log(Level.INFO, msg);
			}
		} catch( Exception exc ) {
			final String msg = "For package " + frameworkPackage.getName() + ", failed to handle package derivation " + derivedFrom;
			LOGGER.log(Level.SEVERE, msg, exc );
//			throw new GenerationException( msg, exc );
		}

		return determinedPath;
	}

	// attributes
	// -----------------------------------------------------------------
	// flag indicating whether a file should be overwritten or now
	// -----------------------------------------------------------------
	protected boolean overWriteFlag 		= true;
	// -----------------------------------------------------------------
	// root path of the technology stack package content being generated against
	// -----------------------------------------------------------------
	protected String packageRootPath 		= null;
	// -----------------------------------------------------------------
	// the root path of the server
	// -----------------------------------------------------------------
	protected String serverRootPath 		= null;
	// -----------------------------------------------------------------
	// the root path to output to
	// -----------------------------------------------------------------
	protected String outputRootPath 		= null;
	// -----------------------------------------------------------------
	// the path of the common template files 
	// -----------------------------------------------------------------
	protected String commonTemplatesPath 	= null;
	// -----------------------------------------------------------------
	// the path of the resource override files
	// -----------------------------------------------------------------
	protected String resourceOverridePath 	= null;
	// -----------------------------------------------------------------
	// the template path in use
	// -----------------------------------------------------------------
	protected String theTemplatePathToUse 	= null;
	// -----------------------------------------------------------------
	// the path of a processed map file
	// -----------------------------------------------------------------
	protected String processedMapFilePath 	= null;
	// -----------------------------------------------------------------
	// the directory mappings held as properties
	// -----------------------------------------------------------------
	protected Properties dirMappings 		= new Properties();
	// -----------------------------------------------------------------
	// property exclusions
	// -----------------------------------------------------------------
	protected Set<String> exclusions 		= new HashSet<>();
	// -----------------------------------------------------------------
	// Apache Velocity Engine 
	// -----------------------------------------------------------------
	private VelocityEngine velocityEngine 	= null;
	// -----------------------------------------------------------------
	// Apache Velocity Context
	// -----------------------------------------------------------------
	private VelocityContext context 		= null;
	// -----------------------------------------------------------------
	// target technology stack package
	// -----------------------------------------------------------------
	protected FrameworkPackage frameworkPackage = null;
	// -----------------------------------------------------------------
	// application generation helper
	// -----------------------------------------------------------------
	protected GenerateAppHelper genAppHelper 	= null;
	// -----------------------------------------------------------------
	// application generation statistics
	// -----------------------------------------------------------------	
	protected GenerateAppStats generateAppStats = null;
	// -----------------------------------------------------------------
	// loaded application generation options
	// -----------------------------------------------------------------	
	protected GenerateAppOptions genAppOptions 	= null;
	// -----------------------------------------------------------------
	// calling user
	// -----------------------------------------------------------------	
	protected User user 						= null;
	// -----------------------------------------------------------------
	// static declarations
	// -----------------------------------------------------------------
	protected static final String EXCLUSIONS_KEY = "exclusions";
	protected static final String CLASSES_INDICATOR = "__classes__";
	protected static final String PACKAGE_NAME_INDICATOR = "$packageName";
	protected static final String CLASS_NAME_INDICATOR = "$className";
	protected static final String APP_NAME_INDICATOR = "$appName";
	protected static final String MACRO_FILE_EXTENSION = ".vm";
	protected static final String PARENT_ONLY = "__parentonly__";
	protected static final String COMMON_TEMPLATES = "common.templates";
	protected static final String POM_PARENT = "pom-parent.xml";
	protected static final String POM_XML = "pom.xml";
	protected static final String BUILD = "build";
	protected static final String CLASS_OBJECT = "classObject";
	protected static final String ASTERISK = "*";
	protected static final String GENERATE_APP_FAILURE = "GenerateApp Failure";
	protected static final String RESOURCES_AVAILABLE = "resources.available";
	protected static final String VM_EXTENSION = "vm";
	
	public static final String MASTER_DIR_MAP_FILE = System.getProperty("MASTER_DIR_MAP_FILE") == null
			? "mappings.properties"
			: System.getProperty("MASTER_DIR_MAP_FILE");
	public static final String CODE_GEN_CATCH_ALL_DIR = System.getProperty("CODE_GEN_CATCH_ALL_DIR") == null
			? File.separator + "code_gen_catch_all"
			: System.getProperty("CODE_GEN_CATCH_ALL_DIR");
	private static final Logger LOGGER = Logger.getLogger(GenerateApp.class.getName());

}
