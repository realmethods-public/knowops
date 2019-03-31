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
package com.cloudmigrate.common.helpers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import com.google.gson.*;

import com.cloudmigrate.entity.FrameworkPackage;
import com.cloudmigrate.entity.LocalModel;
import com.cloudmigrate.entity.User;
import com.cloudmigrate.entity.UserType;
import com.cloudmigrate.entity.ModelType;
import com.cloudmigrate.entity.ScopeType;
import com.cloudmigrate.entity.SaveParams;
import com.cloudmigrate.entity.dao.FrameworkPackageDAO;
import com.cloudmigrate.entity.dao.LocalModelDAO;
import com.cloudmigrate.entity.dao.UserDAO;
import com.cloudmigrate.exception.GenerationException;
import com.cloudmigrate.foundational.common.namespace.FrameworkNameSpace;
import com.cloudmigrate.foundational.common.exception.FrameworkDAOException;
import com.cloudmigrate.foundational.common.exception.ProcessingException;

import com.google.gson.JsonObject;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.*;
import org.w3c.dom.*;

/**
 * Helper class used to provide global level assistance for key operations within the goFramework
 * 
 * @author realMethods, Inc.
 * 
 */
public class AppGenHelper {
	
	/**
	 * Use the self() method to create an instance.
	 */
	protected AppGenHelper() {
		// use factory method self()
	}

	/**
	 * Singleton factory pattern
	 * 
	 * @return
	 */
	public static AppGenHelper self() {
		if (self == null)
			self = new AppGenHelper();

		return (self);
	}

	/*
	 * Factory singleton method
	 * 
	 * @param servletContext
	 * @return AppGenHelper
	 */
	public static AppGenHelper self(ServletContext servletContext) {
		self();

		self.setServletContext(servletContext);

		return (self);
	}

	/**
	 * Used to jump start goFramework. Does so by loading technology stack packages and models it
	 * discovers under the root directory. Each of these exist to populate a
	 * new installation of the goFramework.
	 */
	public void jumpStart() {

		try {
			Long userId = 1L;
			user = new UserDAO().findUser(userId);
		} catch (FrameworkDAOException exc) {
			// force to null if finding fails.  will be null if none found
			user = null;
		}
		
		// ===============================================================
		// see if the the first model is loaded. if not, load them all
		// from the model directory
		// ===============================================================

		try {
			if (user == null) {
				LOGGER.info("AppGenHelper.jumpStart() - creating first user as cloudMigrate...");

				user = createRealMethodsAndCLIUser();

				LOGGER.info("AppGenHelper.jumpStart() - loading local models...");
				loadLocalModels();

				LOGGER.info("AppGenHelper.jumpStart() - loading remote tech stack packages...");
				loadRemoteFrameworkPackages();
			}
		} catch (Exception exc) {
			LOGGER.log(Level.INFO, "AppGenHelper.jumpStart(): ", exc);
		}
	}
	
	/**
	 * Returns the servletContext field.  
	 * 
	 * This field is externally assigned.
	 * 
	 * @return ServletContext
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * Assigns the servletContext field the provided argument.
	 * 
	 * @param context
	 */
	public void setServletContext(ServletContext context) {
		servletContext = context;
	}

	/**
	 * Helper method used to load model_files.properties file.  This file contains  
	 * information about each model file to discover in the models directory.
	 */
	private void loadModelFileProperties() {
		properties = new Properties();

		try {
			properties.load(new java.io.FileReader(getModelPath() + File.separator + "model_files.properties"));
		} catch( Exception exc ) {
			LOGGER.log( Level.SEVERE, "AppGenHelper.loadModelFileProperties", exc);
		}
	}

	/**
	 * Returns the real path to the models directory.
	 * 
	 * @return String
	 */
	private String getModelPath() {
		return (getServletContext().getRealPath("models"));
	}

	/**
	 * Helper method used to load all model files discovered in the models directory under the root.
	 * 
	 * @throws GenerationException
	 */
	protected void loadLocalModels() throws ProcessingException {

		try {
			this.loadModelFileProperties();
		} catch (Exception exc) {
			LOGGER.info("Failed to loadModelFileProperties()" + exc.getMessage());
			throw new ProcessingException( "AppGenHelper.loadLocalModels", exc);
		}
		final LocalModelDAO dao 	= new LocalModelDAO();
		
		properties.entrySet().forEach( entry -> {
			final StringTokenizer tokenizer	= new StringTokenizer(entry.getValue().toString(), ";");
			final LocalModel localModel 	= new LocalModel();
			final String description		= tokenizer.nextToken();
			final String remoteFilePath		= tokenizer.nextToken();
			
			localModel.setName(entry.getKey().toString());
			localModel.setDescription(description);
			localModel.setFilePath(AWSHelper.self().stripAWSUrlParts(remoteFilePath));
			localModel.setOriginalFileName(FilenameUtils.getName(localModel.getFilePath()));
			localModel.setOwnerId(user.getId());
			localModel.setScopeType(ScopeType.PUBLIC);
			localModel.setCheckSum(self.determineCheckSum(remedyFileLocation(remoteFilePath)));

			try {
				dao.create(localModel);
			} catch (Exception exc) {
				LOGGER.info("AppGenHelper.jumpStart() failed to load shared models - " + exc.getMessage());
			}
		});
	}

	/**
	 * Helper method to discover and persist all available tech stack plocated remotely within 
	 * a designated S3 bucket.
	 * 
	 */
	protected void loadRemoteFrameworkPackages() {
		
		try {
			List<String> pkgPaths			= AWSHelper.self().getPublicPackageFromS3Bucket();
			for( String pkgPath : pkgPaths ) {
				if ( pkgPath.endsWith(".zip") ) 
					createFrameworkPackage(AWSHelper.self().getS3BucketLocation() + pkgPath, user.getId());
			}
		} catch( Exception exc ) {
			final String msg = "Failed to load remote tech stack packages";
			LOGGER.log(Level.WARNING, msg, exc);
		}
	}

	/**
	 * Persists a technology stack (framework) package entry using the elements of the provided package file.
	 * 
	 * @param remotePkgDir
	 * @return FrameworkPackage
	 * @throws GenerationException
	 * @throws IOException
	 */
	public FrameworkPackage createFrameworkPackage(String remotePkgFile, Long ownerId) 
			throws GenerationException, IOException, FrameworkDAOException, 
			ProcessingException, java.net.URISyntaxException {

		FrameworkPackage pkg 	= null;
		FrameworkPackageDAO dao = new FrameworkPackageDAO();


		// pull the name from the XML DOM
		try {
			String localZipFile	= remedyFileLocation(remotePkgFile);

			String md5 = determineCheckSum(localZipFile);

			if ( md5 == null ) // unassigned
				throw new ProcessingException( "Unable to calculate checksum for tech stack package" );

			String localPkgDir		= unzip(localZipFile);
			File packageFile 		= new File(localPkgDir + File.separator + TECH_STACK_PKG_FILE_NAME);
			Path packagePath 		= packageFile.toPath();

			// assign data to the newly created tech stack package

			pkg = new FrameworkPackage();
			pkg.setFilePath(AWSHelper.self().stripAWSUrlParts(remotePkgFile) );
			pkg.setPackageXML(new String(Files.readAllBytes(packagePath)));
			pkg.setOwnerId(ownerId);
			pkg.setScopeType(ScopeType.PUBLIC);
			pkg.setCheckSum(md5);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(packageFile);
	
			// assign more data to the newly created tech stack package
			pkg.setName(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes().getNamedItem("name")
					.getNodeValue());
			pkg.setShortName(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes().getNamedItem("shortName")
					.getNodeValue());		
			pkg.setDescription(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes().getNamedItem("description")
					.getNodeValue());					
			pkg.setVersion(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes()
					.getNamedItem("version").getNodeValue());
			pkg.setIconUrl(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes()
					.getNamedItem("icon-url").getNodeValue());
			pkg.setAppType(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes()
					.getNamedItem("app-type").getNodeValue());
			pkg.setInfoPageUrl(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes()
					.getNamedItem("info-page-url").getNodeValue());
			pkg.setReleaseStatus(doc.getElementsByTagName(TECH_STACK_PKG_NODE).item(0).getAttributes()
					.getNamedItem("release-status").getNodeValue());
			
		} catch( Exception exc ) {
			final String msg = "Failed to create a tech stack package for S3 key " + remotePkgFile;
			LOGGER.log( Level.WARNING, msg, exc);
			throw new ProcessingException( "AppGenHelper.createFrameworkPackage: " + msg, exc);
		}

		//********************************************************
		// this level of checking is only required in production
		//********************************************************
		if (dao.findByNameorId(pkg.getName()) != null) {
			if ( FrameworkNameSpace.IN_PRODUCTION ) {
				throw new ProcessingException( "Tech Stack Package " + pkg.getName() + " already exists.  "
						+ "Name must be unique as set in the techstackpackage.xml" );
			}
		}
	
		if (dao.findByChecksum(pkg.getCheckSum()) != null) {
			if ( FrameworkNameSpace.IN_PRODUCTION ) {
				throw new ProcessingException( "Tech Stack Package " + pkg.getName() + " already exists.  "
					+ "Its contents are matched by another already registered tech stack package" );
			}
		}
		
		pkg = dao.create(pkg);

		return (pkg);
	}

	/**
	 * Helper method to persist a CLI and realMethods user, and returns the realMethods user.
	 *  
	 * @return User
	 * @throws FrameworkDAOException
	 */
	protected User createRealMethodsAndCLIUser() throws FrameworkDAOException {

		User cliUser 	= new User();
		UserDAO dao 	= new UserDAO();
		
		cliUser.setFirstName("CLI");
		cliUser.setLastName("User");
		cliUser.setEmail("cli-user@anycompany.com");
		cliUser.setCompany("Any Company");
		cliUser.setUserId("cli-user");
		cliUser.setPassword("14;lkacm");
		cliUser.setUserType(UserType.HOBBYIST);
		dao.create(cliUser);
		
		User rMUser = new User();
		
		rMUser.setFirstName("realMethods");
		rMUser.setLastName("Dev Team");
		rMUser.setEmail("dev@realmethods.com");
		rMUser.setCompany("realMethods");
		rMUser.setUserId("admin");
		rMUser.setPassword("letmein2");
		rMUser.setUserType(UserType.ENTERPRISE);
		
		return dao.create(rMUser);
	}

	/*
	 * Helper method to unzip the file referenced by the provided argument.
	 * 
	 * The provided file is expected to be a zip file.
	 * 
	 * The unzip file's content will be placed within a temporary directory.
	 * 
	 * @return String - root directory of the unzip file
	 */
	public static String unzip( String fileToUnzip ) throws ProcessingException {
		String unzipRootDirectory = null;

		try {

			ZipFile zipFile = new ZipFile(fileToUnzip);

			if (!zipFile.isValidZipFile()) {
				return null;
			}

			// extract the archive to a new temp directory
			unzipRootDirectory = createTempWorkspaceDirectory();
			zipFile.extractAll(unzipRootDirectory);
		} catch (Exception e) {
			throw new ProcessingException( "AppGenHelper.unzip() : " +  e );
		}

		return (unzipRootDirectory);
	}

	public static String remedyFileLocation( String filePathAndName ) {
		
		if (filePathAndName.startsWith("http")) {
			try {
				String justName		= FilenameUtils.getName(filePathAndName);
				String tmpFilePath 	= AppGenHelper.createTempWorkspaceDirectory()
						+ File.separator
						+ justName;
				
				File tmpFile 		= new File(tmpFilePath);
				
				FileUtils.copyURLToFile(new java.net.URL(filePathAndName.replace(justName, 
												java.net.URLEncoder.encode(justName, "UTF-8"))), tmpFile);
				filePathAndName = tmpFilePath;

			} catch (java.io.IOException exc) {
				final String msg = "Unable to locate the package file " + filePathAndName;
				LOGGER.log(Level.SEVERE, msg, exc );
			}
		}
		return filePathAndName;
	}
	
	/**
	 * Creates a temporary workspace directory. 
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String createTempWorkspaceDirectory() throws IOException {
		String tmpdir = Files.createTempDirectory("rm_").toFile().getAbsolutePath();
		final String msg = "FileHandler.createTempWorkspaceDirectory dir is " + tmpdir;
		LOGGER.log(Level.INFO, msg );
		
		return (tmpdir);
	}

	/**
	 * Extracts the package options of the technology stack package referenced by the provided argument.
	 * 
	 * @param packageId
	 * @return String
	 */
	public static String getPackageOptions( Long packageId ) {

		StringBuilder content 	= new StringBuilder( "<" + PKG_OPTIONS_NODE_NAME + ">");
		String [] extensions 	= {"xml"};
		String fileToLookFor 	= TECH_STACK_OPTIONS_FILE_NAME;
		
		try {
			FrameworkPackage frameworkPackage = new FrameworkPackageDAO().findFrameworkPackage( packageId );
			
			if ( frameworkPackage != null )
			{
				content.append( fileRecurseConcatHelper( unzip( 
															remedyFileLocation(
																AWSHelper.self().getS3BucketLocation() 
																+ frameworkPackage.getFilePath())), 
								extensions, fileToLookFor )  );
				content.append( "</" + PKG_OPTIONS_NODE_NAME + ">" );
				
			}
		}
		catch( Exception exc ) {	
			LOGGER.log( Level.INFO, "AppGenHelper.getPackageOptions()", exc);
		}
		
		return( content.toString() );
	
	}
	
	/**
	 * Helper method used to recurse a directory structure, starting at the workingDir, opens each file according to the
	 * fileToLookFor, reads it's content, and concatenates it to a StringBuilder which is finally returned.
	 * 
	 * @param workingDir
	 * @param extensions
	 * @param fileToLookFor
	 * @return StringBuilder
	 */
	public static StringBuilder fileRecurseConcatHelper(String workingDir, String[] extensions, String fileToLookFor) {

		StringBuilder content 		=	 new StringBuilder("");
		boolean recursive 				= true;
		Iterator<java.io.File> files 	= FileUtils.iterateFiles(new java.io.File(workingDir), extensions, recursive);
		
		files.forEachRemaining( file -> {
			// only care about the
			if (fileToLookFor.equalsIgnoreCase(file.getName())) {
				try {
					content.append(new String(Files.readAllBytes(FileSystems.getDefault()
							.getPath(workingDir, fileToLookFor).resolve(file.getAbsolutePath()))));
				} catch (java.io.IOException exc) {
					LOGGER.info(exc.getMessage());
				}
			}
		});

		return (content);
	}


	/**
	 * Helper method to persist the provided data as a model.
	 * 
	 * @param fileName
	 * @param systemFileName
	 * @param saveParams
	 */
	public static void saveModel( User user, 
									String fileName, 
									String systemFileName, 
									SaveParams saveParams )
	{
		try {
			// use the systemFileName to store it on AWS in the proper S3
			String contentType = null; // use the default
			String s3Path = AWSHelper.self().copyFileToS3Bucket(systemFileName, 
												AWSHelper.self().buildUserPath(user), contentType); 
			
			// persist the model
			LocalModelDAO dao 			= new LocalModelDAO();
			LocalModel sharedLocalModel = new LocalModel();

			sharedLocalModel.setCheckSum( self.determineCheckSum(systemFileName) );
			sharedLocalModel.setOwnerId(user.getId());
			sharedLocalModel.setName(saveParams.getName());
			sharedLocalModel.setDescription(saveParams.getDescription());
			sharedLocalModel.setOriginalFileName(fileName);
			sharedLocalModel.setFilePath(s3Path);
			sharedLocalModel.setModelType(ModelType.deduceTypeFromFileName(fileName));

			dao.create(sharedLocalModel);
		} catch( Exception exc ) {
			final String msg = "Failure to save model file " + systemFileName;
			LOGGER.log( Level.WARNING, msg, exc);
		}
		
	}

	/**
	 * Dissect the input options using the groupDelim as the separator of each grouping, and 
	 * the optionsDelim as the separate for each option within a group.
	 * 
	 * @param inputOptions
	 * @param groupDelim
	 * @param optionsDelim
	 * @return Map<String,String>
	 */
	public static Map<String, String> dissectOptions( String inputOptions, String groupDelim, String optionsDelim ) {

		String key;
		String val;
		StringTokenizer tokenizerA = new StringTokenizer(inputOptions, groupDelim);
		StringTokenizer tokenizerB = null;
		Map<String,String> options = new HashMap<>();

		while (tokenizerA.hasMoreTokens()) {
			tokenizerB = new StringTokenizer(tokenizerA.nextToken(), optionsDelim);

			if (tokenizerB.hasMoreTokens()) {
				key = tokenizerB.nextToken();
				val = "";

				if (tokenizerB.hasMoreTokens())
					val = tokenizerB.nextToken();
				options.put(key, val );
			}
		}
		
		return options;
	}

	/**
	 * Dissect the input options as a Json Array of arrays of options.
	 * 
	 * @param inputOptions
	 * @return Map<String,String>
	 */
	public static Map<String, String> dissectOptions( JsonObject optionsAsJson ) {

		Map<String, String> options = new HashMap<>();
	    try
	    {
	    	String key;
	    	java.util.Iterator<JsonElement> arrays;
	    	JsonObject object;
	    	for( Map.Entry<String,JsonElement> entry :	optionsAsJson.entrySet() ) {
	    		key = entry.getKey();
	    		arrays = ((JsonArray)entry.getValue()).iterator();
	    		while( arrays.hasNext() ) {
	    			object = (JsonObject)arrays.next();
	    			options.put(key + "." + object.get("name").getAsString(), object.get("value").getAsString());
	    		}
	    	}
	    }
	    catch (Exception e)
	    {
	    	LOGGER.log( Level.SEVERE, "Failed to dissect options from JsonObject", e);
	        throw new RuntimeException("Couldnt parse json:" + optionsAsJson.toString(), e);
	    }
	    
	    final String msg = "Dissected options are " + options.toString();
	    LOGGER.info( msg );
	    
	    return options;
	}

	public static InputStream determineInputStream( String fullFilePath ) throws Exception {
		if ( fullFilePath == null || fullFilePath.isEmpty() ) {
			final String msg = "fullFilePath arg is null or empty";
			LOGGER.severe( msg );
			throw new Exception( "AppGenHelper:determineInputStream : " + msg );
		}
		
		if ( fullFilePath.startsWith("http") ) 
			return new URL(fullFilePath).openStream();
		else // assume from a file
			return new FileInputStream(fullFilePath);

	}
	
	public String determineCheckSum( String file ) {
		String checkSum = null;
		try (InputStream is = Files.newInputStream(Paths.get(file))) {
		    checkSum = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
		}		
		catch (Exception exc ) {
			final String msg = "Checksum calc failed on " + file;
			LOGGER.log(Level.SEVERE, msg, exc);
		}
		return checkSum;
	}
	
	public void compressZipfile(String sourceDir, String outputFile, String rootDirName ) throws IOException, FileNotFoundException {
	    ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
	    compressDirectoryToZipfile(sourceDir, sourceDir, zipFile, rootDirName );
	    IOUtils.closeQuietly(zipFile);
	}

	private void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out, String rootDirName) throws IOException, FileNotFoundException {
	    for (File file : new File(sourceDir).listFiles()) {
	        if (file.isDirectory()) {
	            compressDirectoryToZipfile(rootDir, sourceDir + File.separator + file.getName(), out, rootDirName);
	        } else {
	        	String path = rootDirName;
	        	if ( sourceDir.equalsIgnoreCase( rootDir ) == false )
	        		path += sourceDir.replace(rootDir, "");
	        	path += File.separator + file.getName();
	            ZipEntry entry = new ZipEntry(path);
	            out.putNextEntry(entry);

	            FileInputStream in = new FileInputStream(sourceDir +  File.separator + file.getName());
	            IOUtils.copy(in, out);
	            IOUtils.closeQuietly(in);
	        }
	    }
	}
	
	// attributes
	protected static AppGenHelper self 					= null;
	protected ServletContext servletContext 			= null;
	protected Properties properties 					= null;
	private User user 									= null;
	private static final String TECH_STACK_PKG_NODE 	= "tech-stack-package";
	private static final String TECH_STACK_PKG_FILE_NAME = "techstackpackage.xml";
	private static final String TECH_STACK_OPTIONS_FILE_NAME = "options.xml";
	public static final String PKG_OPTIONS_NODE_NAME	= "pkg_options";
	public static final String MODEL_NAME 				= "GO_FRAMEWORK_MODEL_NAME";
	private static final Logger LOGGER 					= Logger.getLogger(AppGenHelper.class.getName());	

}
