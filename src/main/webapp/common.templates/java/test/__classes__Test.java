#header()
#set( $className = $classObject.getName() )
package ${aib.getRootPackageName(true)}.#getTestPackageName();

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

#set( $imports = [ "#getPrimaryKeyPackageName()", "#getDelegatePackageName()", "#getBOPackageName()" ] )
#importStatements( $imports )

/**
 * Test ${classObject.getName()} class.
 *
 * @author    $aib.getAuthor()
 */
public class ${classObject.getName()}Test
{

// constructors

    public ${classObject.getName()}Test()
    {
    	LOGGER.setUseParentHandlers(false);	// only want to output to the provided LogHandler
    }

// test methods
    @Test
    /** 
     * Full Create-Read-Update-Delete of a ${classObject.getName()}, through a ${classObject.getName()}Test.
     */
    public void testCRUD()
    throws Throwable
   {        
        try
        {
        	LOGGER.info( "**********************************************************" );
            LOGGER.info( "Beginning full test on ${classObject.getName()}Test..." );
            
            testCreate();            
            testRead();        
            testUpdate();
            testGetAll();                
            testDelete();
            
            LOGGER.info( "Successfully ran a full test on ${classObject.getName()}Test..." );
            LOGGER.info( "**********************************************************" );
            LOGGER.info( "" );
        }
        catch( Throwable e )
        {
            throw e;
        }
        finally 
        {
        	if ( handler != null ) {
        		handler.flush();
        		LOGGER.removeHandler(handler);
        	}
        }
   }

    /** 
     * Tests creating a new ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}
     */
    public ${classObject.getName()} testCreate()
    throws Throwable
    {
        ${classObject.getName()} businessObject = null;

    	{
	        LOGGER.info( "${classObject.getName()}Test:testCreate()" );
	        LOGGER.info( "-- Attempting to create a ${classObject.getName()}");
	
	        StringBuilder msg = new StringBuilder( "-- Failed to create a ${classObject.getName()}" );
	
	        try 
	        {            
	            businessObject = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().create${classObject.getName()}( getNewBO() );
	            assertNotNull( businessObject, msg.toString() );
	
	            thePrimaryKey = (${classObject.getName()}PrimaryKey)businessObject.get${className}PrimaryKey();
	            assertNotNull( thePrimaryKey, msg.toString() + " Contains a null primary key" );
	
	            LOGGER.info( "-- Successfully created a ${classObject.getName()} with primary key" + thePrimaryKey );
	        }
	        catch (Exception e) 
	        {
	            LOGGER.warning( unexpectedErrorMsg );
	            LOGGER.warning( msg.toString() + businessObject );
	            
	            throw e;
	        }
    	}
        return businessObject;
    }

    /** 
     * Tests reading a ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}  
     */
    public ${classObject.getName()} testRead()
    throws Throwable
    {
        LOGGER.info( "${classObject.getName()}Test:testRead()" );
        LOGGER.info( "-- Reading a previously created ${classObject.getName()}" );

        ${classObject.getName()} businessObject = null;
        StringBuilder msg = new StringBuilder( "-- Failed to read ${classObject.getName()} with primary key" );
        msg.append( thePrimaryKey );

        try
        {
            businessObject = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().get${classObject.getName()}( thePrimaryKey );
            
            assertNotNull( businessObject,msg.toString() );

            LOGGER.info( "-- Successfully found ${classObject.getName()} " + businessObject.toString() );
        }
        catch ( Throwable e )
        {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() + " : " + e );
            
            throw e;
        }

        return( businessObject );
    }

    /** 
     * Tests updating a ${classObject.getName()}.
     *
     * @return    ${classObject.getName()}
     */
    public ${classObject.getName()} testUpdate()
    throws Throwable
    {

        LOGGER.info( "${classObject.getName()}Test:testUpdate()" );
        LOGGER.info( "-- Attempting to update a ${classObject.getName()}." );

        StringBuilder msg = new StringBuilder( "Failed to update a ${classObject.getName()} : " );        
        ${classObject.getName()} businessObject = null;
    
        try
        {            
            businessObject = testCreate();
            
            assertNotNull( businessObject, msg.toString() );

            LOGGER.info( "-- Now updating the created ${classObject.getName()}." );
            
            // for use later on...
            thePrimaryKey = (${classObject.getName()}PrimaryKey)businessObject.get${className}PrimaryKey();
            
            ${classObject.getName()}BusinessDelegate proxy = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance();            
            businessObject = proxy.save${classObject.getName()}( businessObject );   
            
            assertNotNull( businessObject, msg.toString()  );

            LOGGER.info( "-- Successfully saved ${classObject.getName()} - " + businessObject.toString() );
        }
        catch ( Throwable e )
        {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() + " : primarykey-" + thePrimaryKey + " : businessObject-" +  businessObject + " : " + e );
            
            throw e;
        }

        return( businessObject );
    }

    /** 
     * Tests deleting a ${classObject.getName()}.
     */
    public void testDelete()
    throws Throwable
    {
        LOGGER.info( "${classObject.getName()}Test:testDelete()" );
        LOGGER.info( "-- Deleting a previously created ${classObject.getName()}." );
        
        try
        {
            ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().delete( thePrimaryKey );
            
            LOGGER.info( "-- Successfully deleted ${classObject.getName()} with primary key " + thePrimaryKey );            
        }
        catch ( Throwable e )
        {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( "-- Failed to delete ${classObject.getName()} with primary key " + thePrimaryKey );
            
            throw e;
        }
    }

    /** 
     * Tests getting all ${classObject.getName()}s.
     *
     * @return    Collection
     */
    public ArrayList<${classObject.getName()}> testGetAll()
    throws Throwable
    {    
        LOGGER.info( "${classObject.getName()}Test:testGetAll() - Retrieving Collection of ${classObject.getName()}s:" );

        StringBuilder msg = new StringBuilder( "-- Failed to get all ${classObject.getName()} : " );        
        ArrayList<${classObject.getName()}> collection  = null;

        try
        {
            // call the static get method on the ${classObject.getName()}BusinessDelegate
            collection = ${classObject.getName()}BusinessDelegate.get${classObject.getName()}Instance().getAll${classObject.getName()}();

            if ( collection == null || collection.size() == 0 )
            {
                LOGGER.warning( unexpectedErrorMsg );
                LOGGER.warning( "-- " + msg.toString() + " Empty collection returned."  );
            }
            else
            {
	            // Now print out the values
	            ${classObject.getName()} currentBO  = null;            
	            Iterator<${classObject.getName()}> iter = collection.iterator();
					
	            while( iter.hasNext() )
	            {
	                // Retrieve the businessObject   
	                currentBO = iter.next();
	                
	                assertNotNull( currentBO,"-- null value object in Collection." );
	                assertNotNull( currentBO.get${className}PrimaryKey(), "-- value object in Collection has a null primary key" );        
	
	                LOGGER.info( " - " + currentBO.toString() );
	            }
            }
        }
        catch ( Throwable e )
        {
            LOGGER.warning( unexpectedErrorMsg );
            LOGGER.warning( msg.toString() );
            
            throw e;
        }

        return( collection );
    }
    
    public ${classObject.getName()}Test setHandler( Handler handler ) {
    	this.handler = handler;
    	LOGGER.addHandler(handler);	// assign so the LOGGER can only output results to the Handler
    	return this;
    }
    
    /** 
     * Returns a new populate ${classObject.getName()}
     * 
     * @return    ${classObject.getName()}
     */    
    protected ${classObject.getName()} getNewBO()
    {
        ${classObject.getName()} newBO = new ${classObject.getName()}();

// AIB : \#defaultBOOutput() 
#defaultTestBOOutput() 
// ~AIB

        return( newBO );
    }
    
// attributes 

    protected ${classObject.getName()}PrimaryKey thePrimaryKey      = null;
	protected Properties frameworkProperties 			= null;
	private final Logger LOGGER = Logger.getLogger(${classObject.getName()}.class.getName());
	private Handler handler = null;
	private String unexpectedErrorMsg = ":::::::::::::: Unexpected Error :::::::::::::::::";
}
