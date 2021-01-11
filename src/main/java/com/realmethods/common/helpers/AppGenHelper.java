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
package com.realmethods.common.helpers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileInputStream;
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
import com.realmethods.api.PojoParams;
import com.realmethods.entity.FrameworkPackage;
import com.realmethods.entity.LocalModel;
import com.realmethods.entity.User;
import com.realmethods.entity.UserType;
import com.realmethods.entity.ModelType;
import com.realmethods.entity.ScopeType;
import com.realmethods.entity.SaveParams;
import com.realmethods.entity.dao.FrameworkPackageDAO;
import com.realmethods.entity.dao.LocalModelDAO;
import com.realmethods.entity.dao.UserDAO;
import com.realmethods.exception.GenerationException;
import com.realmethods.foundational.common.namespace.FrameworkNameSpace;
import com.realmethods.foundational.common.exception.FrameworkDAOException;
import com.realmethods.foundational.common.exception.ProcessingException;

import com.google.gson.JsonObject;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.io.*;
import org.w3c.dom.*;

/**
 * Helper class used to provide global level assistance for key operations
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
			loadRemoteFrameworkPackages();
			if (user == null) {
				LOGGER.info("creating first user as realMethods...");

				user = createRealMethodsAndCLIUsers();
				LOGGER.info("loading remote tech stack packages...");
			} else {
				fixUpAsNeeded();
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
	 * Returns the real path to the root directory.
	 * 
	 * @return String
	 */
	private String getRootPath() {
		return (getServletContext().getRealPath("/"));
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
	 * Returns the real path to the archives directory.
	 * 
	 * @return String
	 */
	private String getArchivePath() {
		return (getServletContext().getRealPath("archive"));
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
			localModel.setCheckSum(self.determineCheckSum(remedyFileLocation(remoteFilePath, "model_")));

			try {
				dao.create(localModel);
			} catch (Exception exc) {
				LOGGER.info("AppGenHelper.jumpStart() failed to load shared models - " + exc.getMessage());
			}
		});
	}

	/**
	 * Helper method to discover and persist all available tech stack located remotely within 
	 * a designated S3 bucket.
	 * 
	 * @ return		flag indicating if remote loading took place
	 */
	public boolean loadRemoteFrameworkPackages() {

		boolean loadedRemotePackages	= false;

		try {
			Long ownerId 					= 1L;
			String filter 					= null;
			ScopeType scopeType				= ScopeType.PUBLIC;
			FrameworkPackageDAO dao			= new FrameworkPackageDAO();
			List<FrameworkPackage> packages	= dao.findAllFrameworkPackage(scopeType, ownerId, filter);

			//==========================================================
			// if the archive directory is empty and there are persisted
			// packages, update the local archive
			//==========================================================
			File[] archiveContents = new File(getArchivePath()).listFiles();
			if ( !packages.isEmpty() && (archiveContents == null 
					|| archiveContents.length <= 1) ) { // could contain a .empty file 
				List<String> pkgPaths	= AWSHelper.self().getPublicPackageFromS3Bucket();
				pkgPaths.iterator().forEachRemaining( pkg -> {
					if ( validateRemotePackagePath(pkg) ) 
						//==========================================================
						// archive the remote package locally
						//==========================================================
						archivePackage( AWSHelper.self().getS3BucketLocation() + pkg );
				});
			}

			if( packages == null || packages.isEmpty() ) {
				List<String> pkgPaths			= AWSHelper.self().getPublicPackageFromS3Bucket();
				boolean createPackageFlag		= true;
				
				// assign return flag to true;
				loadedRemotePackages = true;
				
				for( String pkgPath : pkgPaths ) {
					if ( validateRemotePackagePath( pkgPath ) ) {
						try {
							validateFrameworkPackage(AWSHelper.self().getS3BucketLocation() + pkgPath, user.getId(), createPackageFlag);
						} catch( Exception exc1 ) {
							// eat it and continue on
						}
					}
				}
			}
		} catch( Exception exc ) {
			final String msg = "Failed to load remote tech stack packages";
			LOGGER.log(Level.WARNING, msg, exc);
		}
		
		return ( loadedRemotePackages );
	}

	/**
	 * Validates and conditionally saves a technology stack (framework) package 
	 * entry using the elements of the provided package file.
	 * 
	 * @param pkgFilePath	filename and path to the tech stack package as an archive file 
	 * @param ownerId		who to assign as the owner of the tech stack package
	 * @param createFlag	create (save) the tech stack package if true
	 * @return FrameworkPackage
	 * @throws IOException
	 * 			FrameworkDAOException
	 * 			ProcessingException 
	 */
	public FrameworkPackage validateFrameworkPackage(String pkgFilePath, Long ownerId, boolean createFlag ) 
			throws IOException, FrameworkDAOException, ProcessingException {
		LOGGER.info( pkgFilePath );

		FrameworkPackage pkg 	= null;
		String localZipFile		= null;
		String localPkgDir		= null;
		
		/////////////////////////////////////////////
		// pull the name from the XML DOM
		///////////////////////////////////////////
		try {

			/////////////////////////////////////////////
			// download the pkg file locally...untouch one that is upladed
			///////////////////////////////////////////
			localZipFile = archivePackage( pkgFilePath );

			///////////////////////////////////////////
			// create a checksum for the local package
			///////////////////////////////////////////
			LOGGER.info("Calculating package checksum...");
			String md5 = determineCheckSum(localZipFile);

			///////////////////////////////////////////
			// bail if checksum determination failed
			///////////////////////////////////////////
			if ( md5 == null ) // unassigned
				throw new ProcessingException( "Unable to calculate checksum for tech stack package" );

			///////////////////////////////////////////
			// unzip the tech stack package and assign it's path
			///////////////////////////////////////////
			LOGGER.info("Decompressing package contents...");
			localPkgDir	= unzip(localZipFile, "tech_stack_package_");

			//////////////////////////////////////////////////////////////////////////
			// validate the contents and structure of the technology stack package
			//////////////////////////////////////////////////////////////////////////
			LOGGER.info("Invoking the package validator...");
			if ( !new FrameworkPackageValidator(getRootPath()).validate(localPkgDir) )
				throw new ProcessingException( "Invalid tech stack package.  Please check structure and contents." );
			
			File packageFile	= new File(localPkgDir + File.separator + TECH_STACK_PKG_FILE_NAME);
			Path packagePath 	= packageFile.toPath();

			///////////////////////////////////////////////////////////
			// assign data to an instantiated technology stack package
			///////////////////////////////////////////////////////////
			pkg = new FrameworkPackage();

			///////////////////////////////////////////
			// reassign the zip file to the new location
			// relative to the web root
			///////////////////////////////////////////
			localZipFile = getArchivePath() + File.separator + FilenameUtils.getName(localZipFile);
			localZipFile = localZipFile.replace( getRootPath() , "");
			
			pkg.setLocalZipFilePath(localZipFile);
			pkg.setFilePath(localPkgDir);			
			
			pkg.setPackageXML(new String(Files.readAllBytes(packagePath)));
			pkg.setOwnerId(ownerId);
			pkg.setScopeType(ScopeType.PUBLIC);
			pkg.setCheckSum(md5);
						
			DocumentBuilderFactory dbFactory 	= DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder 			= dbFactory.newDocumentBuilder();
			Document doc 						= dBuilder.parse(packageFile);
	
			////////////////////////////////////////////////////////////////////
			// assign XML def data to the newly created technology stack package
			////////////////////////////////////////////////////////////////////
			LOGGER.info("Parsing techstackpackage.xml DOM...");
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

			Node derivedFromNode = doc.getElementsByTagName(TECH_STACK_PKG_NODE)
									.item(0)
									.getAttributes()
									.getNamedItem("derivedFrom");

			// ======================================================
			// the derivedFrom node  is optional
			// ======================================================
			if ( derivedFromNode != null ) {
				pkg.setDerivedFrom( derivedFromNode.getNodeValue() );
			}

		} catch( Exception exc ) {
			final String msg = "Failed to create a tech stack package";
			LOGGER.log( Level.WARNING, msg, exc);
			throw new ProcessingException( msg, exc);
		}

		final FrameworkPackageDAO dao 	= new FrameworkPackageDAO();

		//********************************************************
		// unique name or id checking is only required in production
		//********************************************************
		LOGGER.info("Testing for name uniqueness...");
		if (dao.findByNameorId(pkg.getName()) != null /*&& FrameworkNameSpace.IN_PRODUCTION*/ ) {
			throw new ProcessingException( "Tech Stack Package " + pkg.getName() + " already exists.  "
				+ "Name must be unique as set in the techstackpackage.xml" );
		}
		LOGGER.info("Passed name uniqueness...");

		//********************************************************
		// unique checksum checking is only required in production
		//********************************************************
		LOGGER.info("Testing for checksum uniqueness...");
		if (dao.findByChecksum(pkg.getCheckSum()) != null /*&& FrameworkNameSpace.IN_PRODUCTION*/ ) {
			throw new ProcessingException( "Tech Stack Package " + pkg.getName() + " already exists.  "
				+ "Its contents checksum are matched by another already registered tech stack package" );
		}
		LOGGER.info("Passed checksum uniqueness...");

		if ( createFlag ) {
			LOGGER.info("Saving tech stack package...");
			pkg = dao.create(pkg);
		}

		return (pkg);
	}

	/**
	 * Helper method to persist a CLI and realMethods user, and returns the realMethods user.
	 *  
	 * @return User
	 * @throws FrameworkDAOException
	 */
	protected User createRealMethodsAndCLIUsers() throws FrameworkDAOException {

		UserDAO dao 		= new UserDAO();

		LOGGER.info( "Creating realMethods Admin user...");

		User rMUser = new User();
		rMUser.setFirstName("realMethods");
		rMUser.setLastName("Dev Team");
		rMUser.setEmail("dev@realmethods.com");
		rMUser.setCompany("realMethods");
		rMUser.setUserId("admin");
		rMUser.setPassword(DEFAULT_PW);
		rMUser.setUserType(UserType.ENTERPRISE);
		rMUser.setInternalIdentifier("sny6iLHAH4SMJU1F");
		dao.create(rMUser);

		LOGGER.info( "Creating CLI general user...");
		
		User cliUser 		= new User();		
		cliUser.setFirstName("CLI");
		cliUser.setLastName("User");
		cliUser.setEmail("cli-user@anycompany.com");
		cliUser.setCompany("Any Company");
		cliUser.setUserId("cli-user");
		cliUser.setPassword("14;lkacm");
		cliUser.setUserType(UserType.PROFESSIONAL);
		cliUser.setInternalIdentifier("URKaJkjaEVIJEEFI");
		dao.create(cliUser);

		LOGGER.info( "Creating general BitBucket user...");

		User bitBucketUser 	= new User();
		bitBucketUser.setFirstName("GitLab");
		bitBucketUser.setLastName("User");
		bitBucketUser.setEmail("gitlab@realmethods.com");
		bitBucketUser.setCompany("GitLab");
		bitBucketUser.setUserId("gitlab");
		bitBucketUser.setPassword("gitlab");
		bitBucketUser.setUserType(UserType.PROFESSIONAL);
		bitBucketUser.setInternalIdentifier("CBc10800RKddRGQh");
		dao.create(bitBucketUser);

		LOGGER.info( "Creating general GitLab user...");
		
		User gitLabUser 	= new User();
		gitLabUser.setFirstName("Bitbucket");
		gitLabUser.setLastName("User");
		gitLabUser.setEmail("bitbucket@realmethods.com");
		gitLabUser.setCompany("Bitbucket");
		gitLabUser.setUserId("bitbucket");
		gitLabUser.setPassword(DEFAULT_PW);
		gitLabUser.setUserType(UserType.PROFESSIONAL);
		gitLabUser.setInternalIdentifier("NXSaoHyVf7uDeqhT");
		dao.create(gitLabUser);

		LOGGER.info( "Creating general CircleCI user...");
		
		User circleCIUser 	= new User();
		circleCIUser.setFirstName("CircleCI");
		circleCIUser.setLastName("User");
		circleCIUser.setEmail("circleci@realmethods.com");
		circleCIUser.setCompany("CircleCI");
		circleCIUser.setUserId("circleci");
		circleCIUser.setPassword(DEFAULT_PW);
		circleCIUser.setUserType(UserType.PROFESSIONAL);
		circleCIUser.setInternalIdentifier("DsJqTpYht3LcKb80");
		dao.create(circleCIUser);

		LOGGER.info( "Creating general CodeSandbox user...");
		
		User codeSandboxUser 	= new User();
		codeSandboxUser.setFirstName("CodeSandbox");
		codeSandboxUser.setLastName("");
		codeSandboxUser.setEmail("info@codesandbox.io");
		codeSandboxUser.setCompany("CodeSandbox");
		codeSandboxUser.setUserId("codesandbox");
		codeSandboxUser.setPassword(DEFAULT_PW);
		codeSandboxUser.setUserType(UserType.PROFESSIONAL);
		codeSandboxUser.setInternalIdentifier("FQFrmI8W4uSiDdNQ");
		dao.create(codeSandboxUser);

		return rMUser;
	}

	/*
	 * Helper method to unzip the file referenced by the provided argument.
	 * 
	 * The provided file is expected to be a zip file.  
	 * 
	 * The unzip file's content will be placed within a temporary directory with a 
	 * default prefix of rm_
	 *
	 * @param	fileToUnzip		file to unzip
	 * @return String - root directory of the unzip file
	 */
	public static String unzip( String fileToUnzip ) throws ProcessingException {
		return unzip( fileToUnzip, DIR_DEFAULT_PREFIX );
	}

	/*
	 * Helper method to unzip the file referenced by the provided argument.
	 * 
	 * The provided file is expected to be a zip file.
	 * 
	 * The unzip file's content will be placed within a temporary directory
	 * named using the provided directory prefix.
	 * 
	 * @param	fileToUnzip		the file to unzip
	 * @param	dirPrefix		the directory prefix
	 * @return 	String 			root directory of the unzip file
	 */
	public static String unzip( String fileToUnzip, String dirPrefix ) throws ProcessingException {
		String unzipRootDirectory = null;

		try {

			ZipFile zipFile = new ZipFile(fileToUnzip);

			if (!zipFile.isValidZipFile()) {
				return null;
			}
			
			// ===============================================================
			// create a temporary working directory
			// ===============================================================
			unzipRootDirectory = createTempWorkspaceDirectory(dirPrefix);

			// ===============================================================
			// extract the archive to a temporary working directory
			// ===============================================================
			zipFile.extractAll(unzipRootDirectory);
		} catch (Exception e) {
			throw new ProcessingException( "AppGenHelper.unzip() : " +  e );
		}

		return (unzipRootDirectory);
	}

	/**
	 * If an http path, downloads the file/files into a temp dir
	 * If an http path and ends with .git, does a git clone into a temp dir
	 * @param filePathAndName
	 * @param dirPrefix
	 * @return temp dir
	 */
	public static String remedyFileLocation( String filePathAndName ) {
		return remedyFileLocation( filePathAndName, DIR_DEFAULT_PREFIX );
	}

	/**
	 * If an http path, downloads the file/files into a temp dir
	 * If an http path and ends with .git, does a git clone into a temp dir
	 * @param filePathAndName
	 * @param dirPrefix
	 * @return temp dir
	 */
	public static String remedyFileLocation( String filePathAndName, String dirPrefix ) {

		// ===============================================================
		// check if this a Url
		// ===============================================================
		if (filePathAndName.startsWith("http")) {
			try {

				String justName		= FilenameUtils.getName(filePathAndName);
				String tmpFilePath 	= AppGenHelper.createTempWorkspaceDirectory(dirPrefix) + File.separator + justName;				
				File tmpFile 		= new File(tmpFilePath);
				
				// ===============================================================
				// copy the contents of the Url to the temporary file path
				// ===============================================================
				FileUtils.copyURLToFile(new java.net.URL(filePathAndName.replace(justName, 
												java.net.URLEncoder.encode(justName, "UTF-8"))), tmpFile);
				filePathAndName = tmpFilePath;

			} catch (Exception exc) {
				final String msg = "Unable to locate the file " + filePathAndName;
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
		return createTempWorkspaceDirectory( DIR_DEFAULT_PREFIX );
	}
	/**
	 * Creates a temporary workspace directory. 
	 * 
	 * @param 	dirPrefix	directory prefix
	 * @return	temporary workspace directory
	 * @throws IOException
	 */
	public static String createTempWorkspaceDirectory( String dirPrefix ) throws IOException {
		String tmpdir = Files.createTempDirectory(dirPrefix).toFile().getAbsolutePath();
		final String msg = "FileHandler.createTempWorkspaceDirectory dir is " + tmpdir;
		LOGGER.log(Level.INFO, msg );
		
		return (tmpdir);
	}

	/**
	 * Extracts the package options of the technology stack package referenced by the provided argument.
	 * 
	 * @param frameworkPackage		tech stack package
	 * @return String
	 */
	public static String getPackageOptions( FrameworkPackage frameworkPackage ) {

		StringBuilder content 	= new StringBuilder();
		String [] extensions 	= {"xml"};
		String fileToLookFor 	= TECH_STACK_OPTIONS_FILE_NAME;
		
		try {
			// ===============================================================
			// Find the framework package using the provided package id
			// ===============================================================
			
			if ( frameworkPackage != null ) {
				content.append( fileRecurseConcatHelper( frameworkPackage.getFilePath(), 
														extensions, fileToLookFor )  );
				String derivedFrom = frameworkPackage.getDerivedFrom();

				// ====================================================================
				// recurse if the derived from another tech stack package
				// ====================================================================
				if ( derivedFrom != null && !derivedFrom.isEmpty() ) {
					content.append( getPackageOptions(new FrameworkPackageDAO().findByNameorId(derivedFrom)) );
				}
			}
		}
		catch( Exception exc ) {	
			LOGGER.log( Level.WARNING, "Error retrieving package option", exc);
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

		StringBuilder content 			=	 new StringBuilder("");
		boolean recursive 				= true;
		Iterator<java.io.File> files 	= FileUtils.iterateFiles(new java.io.File(workingDir), extensions, recursive);
		
		files.forEachRemaining( file -> {
			// only care about the file to look for
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
	public static LocalModel saveModel( User user, 
									String fileName, 
									String systemFileName,
									SaveParams saveParams,
									PojoParams pojoParams )
	{
		LocalModel sharedLocalModel		= null;
		String usersDirectoryAndFile 	= null;

		try {
			// ===================================================================
			// let the Git repo be the file/directory itself
			// ===================================================================
			if ( fileName.endsWith(".git" ) )
				usersDirectoryAndFile = fileName;
			// ===================================================================
			// let's create a unique one in the user directory
			// ===================================================================
			else {
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HHmmss");
				usersDirectoryAndFile = FileUtils.getUserDirectoryPath()
											+ File.separator + "realMethods" + File.separator
											+ "model_" + sdf.format(new java.util.Date()) + "__" + fileName;
				FileUtils.copyFile(new File(systemFileName),new File(usersDirectoryAndFile));
			}

			// ===================================================================
			// create a model
			// ===================================================================
			LocalModelDAO dao 	= new LocalModelDAO();
			sharedLocalModel 	= new LocalModel();

			//sharedLocalModel.setCheckSum( self.determineCheckSum(systemFileName) );
			sharedLocalModel.setOwnerId(user.getId());
			sharedLocalModel.setName(saveParams.getName());
			sharedLocalModel.setDescription(saveParams.getDescription());
			sharedLocalModel.setOriginalFileName(fileName);
			sharedLocalModel.setFilePath(usersDirectoryAndFile);
			sharedLocalModel.setPojoParams(pojoParams);
			
			sharedLocalModel.setModelType(ModelType.deduceTypeFromFileName(fileName));

			dao.create(sharedLocalModel);
		} catch( Exception exc ) {
			final String msg = "Failure to save model file " + systemFileName;
			LOGGER.log( Level.WARNING, msg, exc);
		}
		finally {
			if ( !fileName.endsWith(".git" ) )
				FileUtils.deleteQuietly( new File(fileName) );
		}	

		return sharedLocalModel;
	}

	/**
	 * Default handler to delegate internally to dissectOptions(String,String,String) by providing
	 * a standard group delimeter and options delimeter.
	 * 
	 * @param inputOptions
	 * @return
	 */
	public static Map<String, String> dissectOptions( String inputOptions) {
		return dissectOptions( inputOptions, GROUP_DELIM, OPTIONS_DELIM );
	}

	/**
	 * Disects the input options using the groupDelim as the separator of each grouping, and 
	 * the optionsDelim as the separate for each option within a group.  These are the application options
	 * provided by either a Project-as-Code YAML during a CLI session, or from the UI.
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
    	java.util.Iterator<JsonElement> arrays	= null;
		Map<String, String> options 			= new HashMap<>();
    	JsonObject object						= null;
	  
    	for( Map.Entry<String,JsonElement> entry : optionsAsJson.entrySet() ) {
    		////////////////////////////////////////////////////////////////////////////////////////////
    		// if the related value is an array, iterate over it 
    		// assuming it contains name/value pairs
    		////////////////////////////////////////////////////////////////////////////////////////////
    		if ( entry.getValue() instanceof com.google.gson.JsonArray ) {
    			arrays = ((JsonArray)entry.getValue()).iterator();
    			while( arrays.hasNext() ) {
    				object = (JsonObject)arrays.next();
    				options.put(entry.getKey() + "." + object.get("name").getAsString(), object.get("value").getAsString());
    			}
    		}
    		////////////////////////////////////////////////////////////////////////////////////////////
    		// treat the value as a JsonObject and inspect each of its values
    		////////////////////////////////////////////////////////////////////////////////////////////
    		else { 
    			for( Map.Entry<String,JsonElement> innerEntry :	((JsonObject)entry.getValue()).entrySet() ) {
    				if ( !innerEntry.getValue().isJsonNull() )
    					options.put(entry.getKey() + "." + innerEntry.getKey(), innerEntry.getValue().getAsString());
    				else {
    					final String msg = "Ignoring null value for input option " + innerEntry.getKey();
    					LOGGER.info( msg );
    				}
    			}
    		}
    	}
	    
	    LOGGER.log(Level.INFO, "Dissected options are {0}", options );
	    
	    return options;
	}

	/** 
	 * Returns an input stream depending upon if the fullFilePath arg is a Url (prefixed with http)
	 * or a file path on the file system.
	 * 
	 * @param fullFilePath
	 * @return
	 * @throws ProcessingException
	 */
	public static InputStream determineInputStream( String fullFilePath ) throws ProcessingException {
		if ( fullFilePath == null || fullFilePath.isEmpty() ) {
			final String msg = "fullFilePath arg is null or empty";
			LOGGER.severe( msg );
			throw new ProcessingException( "AppGenHelper:determineInputStream : " + msg );
		}
		
		InputStream inputStream = null;
		
		try {
			if ( fullFilePath.startsWith("http") ) 
				inputStream = new URL(fullFilePath).openStream();
			else // assume from a file
				inputStream =  new FileInputStream(fullFilePath);
		} catch( Exception exc ) {
			throw new ProcessingException( "Failed to locate and open input stream " + fullFilePath, exc );
		}

		return inputStream; 
	}
	
	/**
	 * Helper function used to create a checksum on the provided file String value.
	 * Uses DigestUtils.md5Hex().
	 * @param file
	 * @return
	 */
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
	
	/**
	 * Compress the source directory and it's content into an outputFile located 
	 * at the rootDirName
	 * @param sourceDir
	 * @param outputFile
	 * @param rootDirName
	 * @throws IOException
	 */
	public void compressZipfile(String sourceDir, String outputFile, String rootDirName ) 
			throws IOException {
	    try(ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile))) {
	    compressDirectoryToZipfile(sourceDir, sourceDir, zipFile, rootDirName );
	    }
	}

	/**
	 * Compresses the rootDir to 
	 * @param rootDir		location of dirs/files to zips
	 * @param sourceDir		locaton where to compress to
	 * @param out			output stream to write into
	 * @param rootDirName	
	 * @throws IOException
	 */
	private void compressDirectoryToZipfile(String rootDir, String sourceDir, ZipOutputStream out, String rootDirName) 
			throws IOException {
	    for (File file : new File(sourceDir).listFiles()) {
	        if (file.isDirectory()) {
	            compressDirectoryToZipfile(rootDir, sourceDir + File.separator + file.getName(), out, rootDirName);
	        } else {
	        	StringBuilder path = new StringBuilder(rootDirName);
	        	
	        	if ( !sourceDir.equalsIgnoreCase( rootDir ) )
	        		path.append(sourceDir.replace(rootDir, ""));
	        	
	        	path.append( File.separator + file.getName() );
	        	
	            ZipEntry entry = new ZipEntry(path.toString());
	            out.putNextEntry(entry);

	            try( FileInputStream in = new FileInputStream(sourceDir +  File.separator + file.getName()) ) {
	            	IOUtils.copy(in, out);
	            }
	        }
	    }
	}

	/**
	 * Method used to fixup already loaded data in the case something changes version to version
	 */
	private void fixUpAsNeeded(){
		this.loadModelFileProperties();
		properties.entrySet().forEach( entry -> {
			LocalModelDAO dao 		= new LocalModelDAO();
			try {
				LocalModel localModel 	= (LocalModel)dao.findByName( "LocalModel", entry.getKey().toString());
				
				if ( localModel != null )
					dao.delete( localModel.getId() );
			} catch (Exception exc) {
				LOGGER.log( Level.SEVERE, "Failed to delete dangling models", exc );
			}		
		});		

	}

	/**
	 * Helper method to persist this thread current model to a temp work space directory
	 * 
	 * @return
	 */
	public static synchronized String persistCurrentModelToFileSystem(String fileName) throws IOException {
		String jsonModelFilePath = createTempWorkspaceDirectory("model_") + File.separator + fileName;
		try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(jsonModelFilePath))) {
			writer.write(new com.realmethods.codetemplate.tools.JSONModelHandler().json());
		} catch( Exception exc) {
			LOGGER.log( Level.SEVERE, "Error writing current model to the file system", exc );
			jsonModelFilePath = null;
		}
		return jsonModelFilePath;
	}
	
	/**
	 * Helper method to copy a remote or uploaded package zip file to the archive 
	 *
	 * @param		pkgFilePath		package file path
	 * @return		local path of the downloaded package zip file
	 * @exception	IOException
	 */
	private String archivePackage( String pkgFilePath ) {
		LOGGER.log(Level.INFO,"Copying {0} to archive directory", pkgFilePath );
		///////////////////////////////////////////
		// turn a remote package into a local package
		///////////////////////////////////////////
		String localZipPath = null;
		
		try{
			localZipPath = remedyFileLocation(pkgFilePath, "archive_" );
			
			////////////////////////////////////////////
			// copy the tech stack package to the archive dir
			////////////////////////////////////////////
			FileUtils.copyFileToDirectory( new File(localZipPath), new File( getArchivePath() ) );

		} catch( IOException exc ) {
			LOGGER.log(Level.SEVERE, "Failed to copy {0} to archive directory", pkgFilePath);
		}
		return( localZipPath );
	}

	/**
	 * returns true or false depending upon the package path being to a zip file and
	 * not the common.templates zip file
	 * 
	 * @param	pkgPath		remote S3 path to the package
	 * @return	true/false  if valid
	 */
	private boolean validateRemotePackagePath( String pkgPath ) {
		return (pkgPath != null && pkgPath.endsWith(".zip") );
	}

	// attributes
	
	//////////////////////////////////////////////////////////
	// singleton pattern 
	//================================
	protected static AppGenHelper self 					= null;
	//================================
	// Servlet context
	//================================
	protected ServletContext servletContext 			= null;
	//================================
	// properties read into
	//================================	
	protected Properties properties 					= null;
	//================================
	// calling User
	//================================	
	private User user 									= null;
	//================================
	// static final Strings
	//================================	
	private static final String TECH_STACK_PKG_NODE 	= "tech-stack-package";
	private static final String TECH_STACK_PKG_FILE_NAME = "techstackpackage.xml";
	private static final String TECH_STACK_OPTIONS_FILE_NAME = "options.xml";
	private static final String DIR_DEFAULT_PREFIX		= "rm_";
	public static final String PKG_OPTIONS_NODE_NAME	= "pkg_options";
	public static final String MODEL_NAME 				= "GO_FRAMEWORK_MODEL_NAME";
	public static final String GROUP_DELIM 				= "!";
	public static final String OPTIONS_DELIM			= "|";

	private static final String DEFAULT_PW				= "letmein2";
	
	//================================
	// static final Logger
	//================================	
	private static final Logger LOGGER 					= Logger.getLogger(AppGenHelper.class.getName());	

}
