#header()

#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )

using System;
using System.Collections;
using System.Collections.Generic;
using Microsoft.Extensions.Logging;

using ${appName};
using ${appName}.DAOs;
using ${appName}.Exceptions;
using ${appName}.Models;
using ${appName}.PrimaryKeys;

namespace ${appName}.Delegates
{
    /// <summary>
    /// ${className} business delegate class.
    /// This class implements the Delegate design pattern for the purpose of:
    /// 
    /// 1. Reducing coupling between the business tier and a client of the business tier by hiding all business-tier implementation details</li>
    /// 2. Improving the available of ${className} related services in the case of a ${className} business related service failing.</li>
    /// 3. Exposes a simpler, uniform ${ className} interface to the business tier, making it easy for clients to consume a simple Java object.</li>
    /// 4. Hides the communication protocol that may be required to fulfill ${className} business related services.</li>
    /// @author ${aib.getAuthor()}
    /// </summary>
#set( $declClass = "public class ${className}Delegate" )
#set( $declParentClass = "BaseDelegate" )   
#if ( $classObject.isAbstract() == true )
#set( $declClass = "abstract public class ${className}Delegate" ) 
#end
#if ( $classObject.hasParent() == true )
#set( $declParentClass = "${classObject.getParentName()}Delegate" )
#end
	$declClass : $declParentClass
	{	
	
//************************************************************************
// Public Methods
//************************************************************************

        /// <summary>
        /// default constructor, using dependency injection to acquire a ILogger<${className}> implementation
        /// <param name="_logger"></para>
        /// </summary>
		public ${className}Delegate( ILogger<${className}> _logger )
		{
			logger = _logger;
		}

#if ( $classObject.isAbstract() == false )        
        /// <summmary>
        /// Returns a singleton instance of ${className}Delegate(). 
        /// All methods are expected to be stateless and self-sufficient.
        /// <returns></returns>
        /// </summary>
		public static ${className}Delegate get${className}Instance()
		{
		    if ( singleton == null )
		    {
		    	singleton = new ${className}Delegate( logger );
		    }
		    
		    return( singleton );
		}
 
        /// <summmary>
        /// Retrieve the ${className} via an ${className}PrimaryKey.
        /// <param name="key></para>
        /// <returns></returns>
        /// </summary>        
	    public ${className} get${className}( ${className}PrimaryKey key ) 
	    {
	    	string msgPrefix = "${className}Delegate:get${className} - ";
	        if ( key == null )
	        {
	            string errMsg = msgPrefix + "null key provided.";
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	        
	        ${className} returnBO = null;
	                
	        ${className}DAO dao = get${className}DAO();
	        
	        try
	        {
	            returnBO = dao.find${className}( key );
	        }
	        catch( Exception exc )
	        {
	            string errMsg = "${className}Delegate:get${className}( ${className}PrimaryKey key ) - unable to locate ${className} with key " + key.ToString() + " - " + exc.ToString();
	            logger.LogInformation( errMsg );
	            throw new ProcessingException( errMsg );
	        }
	        finally
	        {
	            release${className}DAO( dao );
	        }        
	        
	        return returnBO;
	    }
	
	
        /// <summmary>
        /// Retrieve a list of all the ${className} models
        /// <returns></returns>
        /// </summary>        
        public List<${className}> getAll${className}() 
	    {
	    	string msgPrefix				= "${className}Delegate:getAll${className}() - ";
	        List<${className}> array		= null;	        
	        ${className}DAO dao 			= get${className}DAO();
	    
	        try
	        {
	            array = dao.findAll${className}();
	        }
	        catch( Exception exc )
	        {
	            string errMsg = msgPrefix + exc.ToString();
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	        finally
	        {
	            release${className}DAO( dao );
	        }        
	        
	        return array;
	    }

        /// <summmary>
        /// Interacts with the persistence tier to create (insert) the provided model
        /// <param name="model"></para>
        /// <returns></returns>
        /// </summary>        
		public ${className} create${className}( ${className} model )
	    {
			string msgPrefix = "${className}Delegate:create${className} - ";
			
			if ( model == null )
	        {
	            string errMsg = msgPrefix + "null model provided";
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	        
	        // return value once persisted
	        ${className}DAO dao  = get${className}DAO();
	        
	        try
	        {
	            model = dao.create${className}( model );
	        }
	        catch (Exception exc)
	        {
	            string errMsg = "${className}Delegate:create${className}() - Unable to create ${className}" + exc.ToString();
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	        finally
	        {
	            release${className}DAO( dao );
	        }        
	        
	        return( model );
	        
	    }
	
        /// <summmary>
        /// Interacts with the persistence tier to save (update) the provided model
        /// <param name="model"></para>
        /// <returns></returns>
        /// </summary>        
        public ${className} save${className}( ${className} model ) 
	    {
	    	string msgPrefix = "${className}Delegate:save${className} - ";
	    	
			if ( model == null )
	        {
	            string errMsg = msgPrefix + "null model provided.";
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
	                
	        ${className}PrimaryKey key = model.get${className}PrimaryKey();
	                    
	        if ( key != null )
	        {
	            ${className}DAO dao = get${className}DAO();
	
	            try
	            {                    
	                model = (${className})dao.save${className}( model );
	            }
	            catch (Exception exc)
	            {
	                string errMsg = "${className}Delegate:save${className}() - Unable to save ${className}" + exc;
	                logger.LogInformation( errMsg );
	                throw ( new ProcessingException( errMsg  ) );
	            }
	            finally
	            {
	                release${className}DAO( dao );
	            }
	            
	        }
	        else
	        {
	            string errMsg = "${className}Delegate:save${className}() - Unable to create ${className} due to it having a null ${className}PrimaryKey.";             
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) );
	        }
			        
	        return( model );
	        
	    }
	   
        /// <summmary>
        /// Deletes the associated model using the provided primary key
        /// <param name="key"></para>
        /// <returns></returns>
        /// </summary>        
        public bool delete( ${className}PrimaryKey key ) 
	    {    	
	    	string msgPrefix 	= "${className}Delegate:save${className} - ";
	    	bool deleted 	= false;
	    	
			if ( key == null )
	        {
	            string errMsg = msgPrefix + "null key provided.";
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException ( errMsg ) );
	        }
	        
	        ${className}DAO dao  = get${className}DAO();
	
	        try
	        {                    
	            deleted = dao.delete${className}( key );
	        }
	        catch (Exception exc)
	        {
	            string errMsg = msgPrefix + "Unable to delete ${className} using key = "  + key + ". " + exc.ToString();
	            logger.LogInformation( errMsg );
	            throw ( new ProcessingException( errMsg ) ); 
	        }
	        finally
	        {
	            release${className}DAO( dao );
	        }
	        		
	        return deleted;
	    }
#end

////////////////////////////////////////////////////////////////////////////
// internal helper methods
////////////////////////////////////////////////////////////////////////////
    
        /// <summmary>
        /// Returns the ${className} specific DAO.
        /// <returns></returns>
        /// </summary>        
        private ${className}DAO get${className}DAO()
	    {
	    	ILoggerFactory logFactory = ApplicationLogger.LoggerFactory;
	        return( new ${className}DAO( logFactory.CreateLogger<${className}DAO>() ) ); 
	    }
	
        /// <summary>
        /// nulls the provided  ${className}DAO
        /// <param name="dao"></para>
        /// </summary>
	    public void release${className}DAO( ${className}DAO dao )
	    {
	        dao = null;
	    }
        
//************************************************************************
// Attributes
//************************************************************************
	    protected static ${className}Delegate singleton = null;
		private static ILogger<${className}> logger;
	}    
}



