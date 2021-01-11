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
package com.realmethods.server.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;
import com.realmethods.entity.User;
import com.realmethods.entity.dao.UserDAO;

/**
 * Struts action class to handle realMethods start up.
 * 
 * Includes an AWS Challenge
 * 
 * @author realMethods, Inc.
 *
 */
public class StartupAction extends BaseStrutsAction {
	
	/**
	 * default constructor
	 */
	public StartupAction() {
		// no_op
	}

	public void setUserId( String userId ) {
		this.userId = userId;
	}
	
	public void setPassword( String password ) {
		this.password = password;
	}
	
	@Override
	/**
	 * Checks to determine if an AWS challenge is necessary.  If the challenge is passed
	 * 
	 * @return	String
	 */
	public String execute() {
		if ( !awsChallenge() )
			return "awschallenge";
		else
			return( SUCCESS );
	}

	/**
	 * Determines whether deployMode and startup is as SaaS or Community Edition (default)
	 * @return
	 */
	public String startUp() {
		// get deployMode as Env Var
		if ( com.realmethods.foundational.common.namespace.FrameworkNameSpace.SAAS_MODE.equalsIgnoreCase( com.realmethods.foundational.common.namespace.FrameworkNameSpace.DEPLOY_MODE ) ) {
			LOGGER.log(Level.INFO,  "Deployed in SaaS Mode" );
			return( "SAAS_SUCCESS");
		}
		else {
			LOGGER.log(Level.INFO,  "Deployed in Community mode" );
			return( SUCCESS );
		}
	}
	
	/**
	 * General logon action.  If userId and password match a User, the user
	 * will be catched
	 * @return
	 */
	public String logon() {
		
		final String msg = "Failure during validating credentials. Try again";
		
		try {
			User user = new UserDAO().findUserByCredentials( userId, password );
			
			if ( user != null ) {
				LOGGER.log(Level.INFO,  "User credentials accepted for userId {0}", userId );
				setUser( user ); // cache the user
				return SUCCESS;
			}
			else
				LOGGER.log(Level.INFO,  "User credentials failed for userId {0}", userId );
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);
		
		return(ERROR);
	}
			
	/**
	 * AWS Challenge to test the user is authorized to run the goFramework.
	 * 
	 * @return boolean
	 */
	protected boolean awsChallenge() {
		boolean passed = true;
		
		final String msg = "AWS challenge : instance id is " 
				+ AWS_INSTANCE_ID + " vs input value of " + awsInstanceId ;
		LOGGER.info( msg );
		
		if ( AWS_INSTANCE_ID != null && AWS_INSTANCE_ID.length() > 0 ) {
			passed = AWS_INSTANCE_ID.equals( awsInstanceId );
			
			if ( passed )
				LOGGER.info( "AWS challenge passed" );
			else
				LOGGER.info( "AWS challenge failed" );
		}
		
		return( passed );
	}

	/*
	 * Assigns the awsInstanceId field the provided argument.
	 * 
	 * @param	awsInstanceId
	 */
	public void setAwsInstanceId( String awsInstanceId ) {
		this.awsInstanceId = awsInstanceId;
	}
	
	public JsonResult getResult() {
		return jsonResult;
	}

	
	// attributes
	protected String awsInstanceId 					= "";
	protected String userId							= null;
	protected String password						= null;
	protected transient JsonResult jsonResult 		= new JsonResult();
	protected transient Gson gson 					= new GsonBuilder().create();	
	protected static final String AWS_INSTANCE_ID 	= System.getProperty( "AWSInstanceId" );
	
	private static final Logger LOGGER 				= Logger.getLogger(StartupAction.class.getName());
	
}
