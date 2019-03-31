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
package com.cloudmigrate.server.action;

import com.google.gson.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.cloudmigrate.api.JsonResult;
import com.cloudmigrate.api.JsonResultCode;
import com.cloudmigrate.entity.User;
import com.cloudmigrate.entity.UserType;
import com.cloudmigrate.entity.dao.UserDAO;
import com.cloudmigrate.foundational.common.mail.SendMailUsingAuthentication;

/**
 * Struts action class to handle User related actions.
 * 
 * @author realMethods, Inc.
 *
 */
public class UserAction extends BaseStrutsAction {
	
	/**
	 * default constructor
	 */
	public UserAction() {
	}

	public void setUserId( String userId ) {
		this.userId = userId;
	}
	
	public void setPassword( String password ) {
		this.password = password;
	}
	
	public void setFirstName(String name) {
		this.firstName = name;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	public void setUserType( String userType ) {
		this.userType = userType;
	}
	
	public void setNotifyFlag( boolean notifyFlag ) {
		this.notifyFlag = notifyFlag;
	}
	
	@Override 
	/**
	 * Checks to determine if an AWS challenge is necessary.  If the challenge is passed
	 * 
	 * @return	String
	 */
	public String execute() {
		return( SUCCESS );
	}

	public String userInfo() {
		User user = getUser();
		
		if (user != null ) {
			this.jsonResult.setResult( gson.toJson(user) );
			jsonResult.setResultCode(JsonResultCode.SUCCESS);
			return SUCCESS;
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage("Failed to acquire user info.  Ensure logged in.");
		return ERROR;
	}

	public String sendPassword() {
		String msg = "Failed to send password...";
		
		try {
			if ( userId != null ) {
				User user = new UserDAO().findUserByUserId( this.userId );
				if ( user != null ) {
					String [] recipients = new String[1];
					recipients[0] = user.getEmail();
					
					SendMailUsingAuthentication mail = new SendMailUsingAuthentication();
					mail.sendEmail(recipients, 
									BaseStrutsAction.labels.getString("PasswordRecoverySubject"), 
									String.format(BaseStrutsAction.labels.getString("PasswordRecoveryMessage"), user.getPassword()));
					
					msg = "Recovery password successfully sent";
					return SUCCESS;
				}
				else
					msg = labels.getString("UseIdLocationFailure");
			}
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);	
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);
		return ERROR;
	}
	
	public String register() {
		String msg = "Failed to save user information...";
		
		try {
			// get user from the session cache
			User user = getUser();

			if ( user != null ) {
				jsonResult.setProcessingMessage(labels.getString("AlreadyLoggedIn"));
				jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
				return ERROR;
			}
			
			// if already taken, be done
			if ( new UserDAO().findUserByUserId(userId) == null ) 
				user = new User();

			else {
				jsonResult.setProcessingMessage(labels.getString("UserIdAlreadyTaken"));
				jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
				return ERROR;
			}
			
			user.setUserId(userId);
			user.setPassword(password);			
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setCompany(company);
			user.setNotifyFlag(notifyFlag);
			user.setUserType(UserType.whichOne(userType));
			
			///////////////////////////////////////////////////////////////////////////
			// 12.29.2019 - 12:41PM
			// may need to do something here if the type has changed, but for now
			// do nothing more than assign
			///////////////////////////////////////////////////////////////////////////
			user.setUserType(UserType.whichOne(this.userType));
			
			msg = "Creating user " + user.toString();
			LOGGER.info(msg);

			new UserDAO().create(user);
	
			sendWelcomeEmail( user );
			
			// apply results
			jsonResult.setResult(gson.toJson(user) );
			jsonResult.setResultCode(JsonResultCode.SUCCESS);			
			return SUCCESS;
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);

		return ERROR;				
		
	}
	
	public String save() {
		String msg = "Failed to save user information...";
		
		try {
			// get user from the session cache
			User user = getUser();

			// if not cached, should not even be here
			if ( user == null ) {
				logoff(); // just in case
				jsonResult.setProcessingMessage("You are not logged in...");
				jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
				return ERROR;
			}

			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setCompany(company);
			user.setNotifyFlag(notifyFlag);
			user.setUserType(UserType.whichOne(userType));
			
			///////////////////////////////////////////////////////////////////////////
			// 12.29.2019 - 12:41PM
			// may need to do something here if the type has changed, but for now
			// do nothing more than assign
			///////////////////////////////////////////////////////////////////////////
			user.setUserType(UserType.whichOne(this.userType));
			
			msg = "Saving user " + user.toString();
			LOGGER.info(msg);
			new UserDAO().save( user );

			// re-cache it
			setUser( user );
			
			// apply results
			jsonResult.setResult(gson.toJson(user) );
			jsonResult.setResultCode(JsonResultCode.SUCCESS);			
			return SUCCESS;
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);
		}

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);

		return ERROR;				
	}
	
	public String resetPassword() {
		
		final String msg = "Unable to reset the password";
		
		try {
			// get user from the session cache
			User user = getUser();
	
			// assign the new password		
			if  (user != null ) {
				user.setPassword(password);
				new UserDAO().save(user);
				
				setUser( user ); // re-cache
				
				jsonResult.setResultCode(JsonResultCode.SUCCESS);

				return SUCCESS;
			}
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);
		}		

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);

		return ERROR;				
	}
	
	public String generateToken() {
		
		final String msg = "Failed during token generation...";
		
		try {
			// get user from the session cache
			User user = getUser();
	
			// assign the new password		
			if  (user != null ) {

				user.generateInternalIdentifier();
				new UserDAO().save(user);

				jsonResult.setResultCode(JsonResultCode.SUCCESS);
				jsonResult.setResult(user.getInternalIdentifier());
								
				return SUCCESS;
			}
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);
		}		

		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);

		return ERROR;				
	}

	public String evalUsersStatus() {
		
		try {
			List<User> users = new UserDAO().findAllUser();
			
			// deal with EVALUATORS
			for( User user : users ) {
				if ( user.getUserType() == UserType.EVALUATOR ) {
					if ( expiringUser( user ) == false ) // expired evaluator
						expiredUser( user );
				} 
				else { // licensed user
					licensedUser( user );
				}
			}
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Failure during eval of users status", exc);
		}
		return SUCCESS;
	}
	
	/**
	 * Returns the jsonResult field.
	 * 
	 * @return
	 */
	public JsonResult getResult() {
		return jsonResult;
	}


	/**
	 * Check if the user is expiring soon.  If so, notify as such
	 * 
	 * @param	User
	 * @return	boolean
	 */
	private boolean expiringUser( User user ) {
		boolean expiring = false;

		if ( user != null && user.getUserType() == UserType.EVALUATOR ) {
			
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(user.DATE_TIME_FORMAT);
//		    LocalDate userDate 			= LocalDate.parse(user.getDateTime(), formatter);
//		    LocalDate now 				= LocalDate.now();
		    
		    // if the userDate + # of days to eval is less than now,
//		    expiring = true;
		    // if the userDate + # of days - notification days < now, send email eval almost up
		    
		}
		
		return expiring;
		
	}
	
	/**
	 * Check if the user is expiring soon.  If so, notify as such
	 * 
	 * @param	User
	 * @return	boolean
	 */
	private boolean expiredUser( User user) {
		boolean expired = false;
		if ( user == null )
			expired = true;
		return expired;
	}
	
	private void sendWelcomeEmail( User user ) {
		String[] recipients 		= new String[1];
		StringWriter writer		= new StringWriter();
		boolean sendAsHTML		= true;
		
		recipients[0] = user.getEmail();
		
		try {
			org.apache.commons.io.IOUtils.copy(new FileInputStream(new File(getTheSession().getServletContext().getRealPath("/") + File.separator + "html/email/welcome.html")), writer);
	
			SendMailUsingAuthentication mail = new SendMailUsingAuthentication();
			
			mail.sendEmail(recipients, 
							BaseStrutsAction.labels.getString("WelcomeMesageSubject"), 
							writer.toString(), sendAsHTML);
		} catch( Exception exc ) {
			LOGGER.log( Level.WARNING, "Issue sending the welcome email", exc);
		}
	}
	
	private void licensedUser( User user ) {
		// to do
	}
	
	// attributes
	protected String firstName 			= null;
	protected String lastName 			= null;
	protected String email 				= null;
	protected String company 			= null;
	protected String userId 			= null;
	protected String password 			= null;
	protected String userType			= UserType.HOBBYIST.toString();
	protected boolean notifyFlag		= true;
	protected JsonResult jsonResult 	= new JsonResult();
	protected Gson gson 				= new GsonBuilder().create();
	private static final Logger LOGGER 	= Logger.getLogger(UserAction.class.getName());
	
}
