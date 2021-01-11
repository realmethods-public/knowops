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
/**
 *  Workfile: $ Revision: $
 *  Last Modified by:   Author: $ on Date: $
 *
 */
package com.realmethods.entity;

import org.apache.commons.io.FilenameUtils;

import com.realmethods.api.PojoParams;

/**
 * Encapsulates data for LocalModel to capture a model's general details
 *
 * @author realMethods, Inc.
 */
public class LocalModel extends BaseTransactionalEntity
{

    /**
     * Default Constructor
     */
    public LocalModel()
    {
    }

    /**
     * Constructor with a LocalModel.
     * 
     * Internally calls copy( LocalModel ).
     * 
     * @param	object	copy source
     */
    public LocalModel(LocalModel object) {
        super();

        if (object == null) {
            throw new IllegalArgumentException("LocalModel( LocalModel object ) - object arg is null.");
        }

        // internal deep copy
        copy(object);
    }

    @Override
    /**
     * Assigns the modelFilePath field using the provided argument.
     * 
     * Uses the extension of the the modelFilePath to assign the modelType field.
     * 
     * @param modelFilePath
     */
    public void setFilePath( String modelFilePath )
    {
    	modelType = ModelType.deduceTypeFromFileName( modelFilePath );
    	
    	super.setFilePath(modelFilePath);
    }

    /**
     * Returns the originalFileName field.
     * 
     * @return String
     */
    public String getOriginalFileName()
    { return this.originalFileName != null ? this.originalFileName : FilenameUtils.getName(getFilePath()); }
    
    /**
     * Assigns the originalFileName field using the provided argument.
     * 
     * @param originalFileName
     */
    public void setOriginalFileName( String originalFileName )
    { this.originalFileName = originalFileName; }



    /**
     * Returns the modelType field.
     * 
     * Defaults to ModelType.getDefaultValue().
     * 
     * @return ModelType
     */
    public ModelType getModelType()
    { return this.modelType; }
    
    /**
     * Assigns the modelType field using the provided argument.
     * 
     * @param modelType
     */
    public void setModelType( ModelType modelType )
    { this.modelType = modelType; }

    /**
     * Returns the pojoParams field.
     * 
     * @return PojoParams
     */
    public PojoParams getPojoParams()
    { return this.pojoParams; }
    
    /**
     * Assigns the pojoParams field using the provided argument.
     * 
     * @param modelType
     */
    public void setPojoParams( PojoParams pojoParams )
    { this.pojoParams = pojoParams; }

    /**
     * Performs a shallow copy.

     * @param object         LocalModel                copy source
     */
    public void shallowCopy(LocalModel object)
    {
        if (object == null) {
            throw new IllegalArgumentException(" LocalModel:shallowCopy(..) - object cannot be null.");
        }

        this.modelType		= object.modelType;
        this.pojoParams		= object.pojoParams;
        
		super.shallowCopy( object );
    }

    /**
     * Performs a deep copy.
     * @param object         LocalModel                copy source
     */
    public void copy(LocalModel object)
    {
        if (object == null) {
            throw new IllegalArgumentException(" LocalModel:copy(..) - object cannot be null.");
        }
        shallowCopy( object);
    }
    /**
     * Returns a string representation of the object.
     * @return String
     */
    @Override
    public String toString()
    {
        StringBuilder returnString = new StringBuilder();
        returnString.append("modelType = " + this.modelType + ", ");
        returnString.append("originalFileName = " + this.originalFileName + ", ");
        returnString.append("pojoParams = " + this.pojoParams + ", ");
        returnString.append( super.toString() );
        
        return returnString.toString();
    }

	@Override
	/**
	 * Hash off of non-null fields id and name.
	 * 
	 * @return	int
	 */
	public int hashCode() {
		return super.hashCode() * modelType.hashCode() ;
	}
	
	@Override
    /**
     * Returns the type of this object.
     * 
     * @return	String
     */
    public String getObjectType() {
        return ("LocalModel");
    }

    @Override
	/**
	 * Comparison method to determine if the provided object is equivalent to this instance.
	 * 
	 * It does so by ensuring it is non-null, of LocalModel type, and the id fields are equivalent.
	 * 
	 * @param	object
	 * @return	boolean
	 */    
    public boolean equals(Object object) {
		return ( super.equals(object) && (object instanceof LocalModel) );
	}

    /**
     * Returns the model type being encapsulated bu the modelType field.
     * 
     * @return String
     */
    public String getAsExtension() {
    	return ModelType.whichExtension(modelType);
    }

// attributes

    protected String originalFileName			= null;
	protected ModelType modelType 				= ModelType.getDefaultValue();
	protected PojoParams pojoParams				= null;
}
