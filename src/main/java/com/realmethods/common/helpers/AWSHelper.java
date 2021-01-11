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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.realmethods.entity.User;
import com.realmethods.foundational.common.exception.ProcessingException;
import com.amazonaws.auth.BasicAWSCredentials;

import org.apache.commons.io.FilenameUtils;

/**
 * Helper class to handle interacting with AWS
 * 
 * @author realMethods, Inc.
 *
 */
public class AWSHelper {

	protected AWSHelper() {
		awsCreds = new com.amazonaws.auth.BasicAWSCredentials(AWS_ACCESSKEY_ID, AWS_SECRET_ACCESS_KEY);
		s3Client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new com.amazonaws.auth.AWSStaticCredentialsProvider(awsCreds))
				  .withRegion(com.amazonaws.regions.Regions.fromName(AWS_REGION))
				  .build();
	}

	public static AWSHelper self() {
		if (self == null) {
			self = new AWSHelper();
		}

		return self;
	}

	/**
	 * Helper method to return an array of references under the folderKey
	 * (folder name) of the S3 bucket.
	 * 
	 * @param folderKey
	 * @return
	 * @throws Exception
	 */
	public List<String> getPublicPackageFromS3Bucket()
			throws ProcessingException {

		List<String> keys = new ArrayList<>();
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(AWS_S3_BUCKET_NAME)
					.withPrefix(PUBLIC_RM_TECH_STACKS_ROOT_DIR);

			ObjectListing objects = s3Client.listObjects(listObjectsRequest);
			for (;;) {
				List<S3ObjectSummary> summaries = objects.getObjectSummaries();
				if (summaries.isEmpty()) {
					break;
				}
				summaries.forEach(s -> keys.add(s.getKey()));
				objects = s3Client.listNextBatchOfObjects(objects);
			}
		} catch (Exception exc) {
			final String msg = "Failed to load tech stack package keys from dedicated AWS S3 Bucket";
			LOGGER.log(Level.SEVERE, msg, exc);
			throw new ProcessingException( msg, exc );
		}
		return keys;
	}

	public String copyFileToS3Bucket(String copySrc, String copyDestDir,
			String contentType) {
		if (copySrc != null && copyDestDir != null) {
			try {
				// append the copySrc fileName to the dest dir
				copyDestDir += "/" + FilenameUtils.getName(copySrc);
				
				// Upload a text string as a new object.
				s3Client.putObject(AWS_S3_BUCKET_NAME, copyDestDir,
						"Uploaded String Object");

				// Upload a file as a new object with ContentType and title
				// specified.
				PutObjectRequest request = new PutObjectRequest(
						AWS_S3_BUCKET_NAME, copyDestDir,
						new java.io.File(copySrc)).withCannedAcl(
								CannedAccessControlList.PublicRead);
				ObjectMetadata metadata = new ObjectMetadata();
				if (contentType != null)
					metadata.setContentType(contentType);
				else
					metadata.setContentType("plain/text");
				metadata.addUserMetadata("x-amz-meta-title",
						"goFramework Archive File");
				request.setMetadata(metadata);
				s3Client.putObject(request);
			} catch (Exception exc) {
				final String msg = "Failed to copy file to S3 Bucket";
				LOGGER.log(Level.SEVERE, msg, exc);
			}
		}

		return getS3BucketLocation() + copyDestDir;
	}

	/**
	 * The true http URL of the AWS S3 bucket
	 * 
	 * @return
	 */
	public String getS3BucketLocation() {
		return (AWS_PROTOCOL + AWS_S3_SERVER_NAME + AWS_S3_BUCKET_NAME + '/');
	}

	public void deleteS3BucketFile( String filePath ) throws ProcessingException {
		 try {
			 LOGGER.log(Level.INFO,  "filePath is {0}", filePath );
	            s3Client.deleteObject(new DeleteObjectRequest(AWS_S3_BUCKET_NAME, filePath));
		 }
	     catch(Exception exc ) {
	            throw new ProcessingException("Failed to delete file from S3 Bucket.");
	        }
	}

	public String stripAWSUrlParts( String inputPathOnAWS ) {
		return inputPathOnAWS.replace(AWS_PROTOCOL,"")
					.replace(AWS_S3_BUCKET_NAME + '.' + AWS_S3_SERVER_NAME,"")
					.replace(AWS_S3_SERVER_NAME,"")
					.replace(AWS_S3_BUCKET_NAME + '/',"");
	}
	
	public String buildUserPath( User user ) {
		return S3_USERS_SUBDIR + user.getInternalIdentifier();
	}
	
	
	// attributes
	private static AWSHelper self 			= null;
	protected BasicAWSCredentials awsCreds 	= null;
	protected AmazonS3 s3Client 			= null;

	public static final String AWS_REGION	= System
			.getProperty("aws.region");
	public static final String AWS_PROTOCOL = System
			.getProperty("aws.protocol");
	public static final String AWS_S3_SERVER_NAME = System
			.getProperty("aws.s3.server.name");
	public static final String AWS_S3_BUCKET_NAME = System
			.getProperty("aws.s3.bucket.name");
	public static final String PUBLIC_RM_TECH_STACKS_ROOT_DIR = System
			.getProperty("public.rm.tech.stacks.root.dir");
	public static final String S3_USERS_SUBDIR = System
			.getProperty("s3.users.subdir");
	public static final String COMMON_TEMPLATES_FILE = System
			.getProperty("common.templates.file");
	// ========================================================
	// AWS acces and secret key are stored in reverse sequence
	// for more security
	// ========================================================
	public static final String AWS_ACCESSKEY_ID = new StringBuilder(System
			.getProperty("aws.accessKeyId")).reverse().toString();
	public static final String AWS_SECRET_ACCESS_KEY = new StringBuilder(System
			.getProperty("aws.secretAccessKey")).reverse().toString();
	
	private static final Logger LOGGER = Logger
			.getLogger(AWSHelper.class.getName());
	
}
