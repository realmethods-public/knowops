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
package com.realmethods.api;

import com.google.gson.*;

import com.realmethods.entity.ModelType;
import com.realmethods.entity.ResourceType;
import com.realmethods.entity.SaveParams;
import com.realmethods.entity.ScopeType;

/**
 * Container class for the RESTful API aspect of goFramework to encapsulate the concept of its
 * input parameters.
 * 
 * @author realMethods, Inc.
 *
 */
public class JsonInput
{

	/**
	 * Returns the cost field.
	 * 
	 * @return
	 */
	public Float getCost() {
		return this.cost;
	}

	/**
	 * Assigns the cost field the provided argument.
	 * 
	 * @param cost
	 */
	public void setCost( Float cost ) {
		this.cost = cost;
	}

	/**
	 * Returns the techStackPackageId field.
	 * 
	 * @return
	 */
	public String getTechStackPackageId() {
		return this.techStackPackageId;
	}

	/**
	 * Assigns the techStackPackageId field the provided argument.
	 * 
	 * @param id
	 */
	public void setTechStackPackageId( String id ) {
		this.techStackPackageId = id;
	}

	/**
	 * Returns the token field.
	 * 
	 * @return
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * Assigns the token field the provided argument.
	 * 
	 * @param id
	 */
	public void setToken( String token ) {
		this.token = token;
	}

	/**
	 * Returns the accessKey field.  
	 * 
	 * An filter is a unique value returned during 
	 * certain service invocations that is in turn required as input for other service invocations.
	 * 
	 * @return
	 */
	public String getFilter() {
		return this.filter;
	}

	/**
	 * Assigns the filter field the provided argument.
	 * 
	 * An accessKey is a unique value returned during 
	 * certain service invocations that is in turn required as input for other service invocations. 
	 * 
	 * @param accessKey
	 */
	public void setFilter( String filter ) {
		this.filter = filter;
	}

	/**
	 * Returns the modelId field.  
	 * 
	 * This field is the unique identifier of a previously loaded and saved model.
	 * 
	 * @return
	 */
	public Long getModelId() {
		return this.modelId;
	}

	/** 
	 * Assigns the modelId field the provided argument.
	 * 
	 * This field is the unique identifier of a previously loaded and saved model.
	 * 	
	 * @param modelId
	 */
	public void setModelId( Long modelId ) {
		this.modelId = modelId;
	}

	/**
	 * Returns the generatedAppId field.  
	 * 
	 * This field is the unique identifier of a previously loaded and saved model.
	 * 
	 * @return
	 */
	public Long getGeneratedAppId() {
		return this.generatedAppId;
	}

	/** 
	 * Assigns the generatedAppId field the provided argument.
	 * 
	 * This field is the unique identifier of a previously loaded and saved model.
	 * 	
	 * @param generatedAppId
	 */
	public void setGeneratedAppId( Long generatedAppId ) {
		this.generatedAppId = generatedAppId;
	}

	/**
	 * Returns the resourceId field.  
	 * 
	 * This field is the unique identifier of a resource.
	 * 
	 * @return
	 */
	public Long getResourceId() {
		return this.resourceId;
	}

	/** 
	 * Assigns the resourceId field the provided argument.
	 * 
	 * This field is the unique identifier of a resource.
	 * 	
	 * @param resourceId
	 */
	public void setResourceId( Long resourceId ) {
		this.resourceId = resourceId;
	}
	/**
	 * Returns the s3FileLocation field.
	 * 
	 * @return
	 */
	public String getS3FileLocation() {
		return this.s3FileLocation;
	}
	
	/**
	 * Assigns the s3FileLocation field the provided argument.
	 * 
	 * @param s3FileLocation
	 */
	public void setS3FileLocation( String s3FileLocation ) {
		this.s3FileLocation = s3FileLocation;
	}

	/**
	 * Returns the pojoParams field.
	 * 
	 * @return
	 */
	public PojoParams getPojoParams() {
		return this.pojoParams;
	}
	
	/**
	 * Assigns the pojoParams field the provided argument.
	 * 
	 * @param pojoParams
	 */
	public void setPojoParams( PojoParams pojoParams ) {
		this.pojoParams = pojoParams;
	}
	

	/**
	 * Returns the modelType field.
	 * 
	 * @return
	 */
	public ModelType getModelType() {
		return this.modelType;
	}

	/**
	 * Assigns the modelType field the provided argument.
	 * 
	 * @param type
	 */
	public void setModelType( ModelType type ) {
		modelType = type;
	}


	/**
	 * Returns the resourceType field.
	 * 
	 * @return
	 */
	public ResourceType getResourceType() {
		return this.resourceType;
	}

	/**
	 * Assigns the resourceType field the provided argument.
	 * 
	 * @param type
	 */
	public void setResourceType( ResourceType type ) {
		resourceType = type;
	}
	/**
	 * Returns the serviceRequestType field.
	 * 
	 * @return
	 */
	public JsonServiceRequestType getServiceRequestType() {
		return this.serviceRequestType;
	}

	/**
	 * Assigns the modelType field the provided argument.
	 * 
	 * @param type
	 */
	public void setServiceRequestType( JsonServiceRequestType serviceRequestType ) {
		this.serviceRequestType = serviceRequestType;
	}

	/**
	 * Returns the gitParams field.
	 * 
	 * @return
	 */
	public GitParams getGitParams() {
		return this.gitParams;
	}
	
	/**
	 * Assigns the gitParams field the provided argument.
	 * 
	 * @param params
	 */
	public void setGitParams( GitParams params ) {
		this.gitParams = params;
	}

	/**
	 * Returns the appOptions field.
	 * 
	 * @return
	 */
	public JsonObject getAppOptions() {
		return this.appOptions;
	}
	
	/**
	 * Assigns the appOptions field the provided argument.
	 * 
	 * @param options
	 */
	public void setAppOptions( JsonObject options ) {
		this.appOptions = options;
	}

	/**
	 * Returns the saveParams field.
	 * 
	 * @return
	 */
	public SaveParams getSaveParams() {
		return this.saveParams;
	}
	
	/**
	 * Assigns the saveParams field the provided argument.
	 * 
	 * @param saveParams
	 */
	public void setSaveParams( SaveParams saveParams ) {
		this.saveParams = saveParams;
	}

	/**
	 * Return the useGit field.  Defaults to false.
	 * 
	 * When true, the gitParams field is expected to be populated with required data.
	 * 
	 * @return
	 */
	public boolean getUseGit() {
		return this.useGit;
	}

	/**
	 * Assigns the useGit field the provided argument.
	 * 
	 * When assigned to true, the gitParams field is expected to be populated with required data.
	 * 
	 * @param useGit
	 */
	public void setUseGit( boolean useGit ) {
		this.useGit = useGit;
	}

	/**
	 * Returns the scope field.
	 * 
	 * @return 	ScopeType
	 */
	public ScopeType getScopeType() {
		return scopeType;
	}
	
	/**
	 * Assigns the scope field using the provided argument
	 * 
	 * @param	scopeType
	 */
	public void setScopeType( ScopeType scopeType ) {
		this.scopeType = scopeType;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("token:" + token );
		builder.append(", techStackPackageId:" + techStackPackageId );
		builder.append(", modelId:" + modelId );
		builder.append(", generatedAppId:" + generatedAppId);
		builder.append(", resourceId:" + resourceId);
		builder.append(", s3FileLocation:" + s3FileLocation );
		builder.append(", modelType:" + modelType );
		builder.append(", filter:" + filter );
		builder.append(", serviceRequestType:" + serviceRequestType );
		builder.append(", scopeType:" + scopeType );
		builder.append(", resourceType:" + resourceType );
		builder.append(", gitParams:" + gitParams);
		builder.append(", appOptions:" + appOptions);
		builder.append(", cost:" + cost);
		builder.append(", pojoParams:" + pojoParams);
		
		return builder.toString();
	}
	
// attributes
	
	protected String token							= null;
	protected String techStackPackageId				= null;
	protected String s3FileLocation					= null;	
	protected String filter							= null;
	protected Long modelId 							= null;	
	protected Long generatedAppId 					= null;
	protected Long resourceId						= null;
	protected Float cost 							= null;
	protected boolean useGit						= true;
	protected ScopeType scopeType					= ScopeType.getDefaultValue();
	protected ModelType modelType 					= ModelType.getDefaultValue();
	protected ResourceType resourceType 			= ResourceType.getDefaultValue();
	protected JsonServiceRequestType serviceRequestType = JsonServiceRequestType.getDefaultValue();
	protected GitParams gitParams 					= new GitParams();
	protected JsonObject appOptions 				= new JsonObject();
	protected SaveParams saveParams 				= new SaveParams();
	protected PojoParams pojoParams					= new PojoParams();

}
			