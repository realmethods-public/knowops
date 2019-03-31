#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
using System;
using System.Collections;
using System.Collections.Generic;
using Microsoft.Extensions.Logging;
using NHibernate;
using NHibernate.Cfg;

using ${appName}.Exceptions;
using ${appName}.Models;
using ${appName}.PrimaryKeys;
using ${appName}.Persistence;

namespace ${appName}.DAOs
{
    /// <summary>
    /// Responsible for interacting with the ORM abstraction to create, read, update, 
    /// and delete an ${appName} entity along with its associated entities
    /// </summary>
	public class ${className}DAO
	{ 
        ///<summary>
        /// default constructor, using dependency injection to acquire an ILogger<${className}DAO> interface
        /// <param name="_logger|></para>
        /// </summary>
		public ${className}DAO( ILogger<${className}DAO> _logger )
		{
			logger = _logger;
		}
		
        ///<summary>
        /// Retrieves a ${className} from the persistent store, using the provided primary key. 
        /// If no match is found, a null ${className} is returned.
        /// <paramm name="pk"></para>
        /// </summary>

    	public ${className} find${className}( ${className}PrimaryKey pk ) 
    	{
    		$className model = null;
    		
        	if (pk == null)
        	{
            	throw( new ProcessingException("${className}DAO.find${className}(...) cannot have a null primary key argument") );
        	}

			using (ISession session = FrameworkPersistenceHelper.OpenSession())
			{
	            using (ITransaction transaction = session.BeginTransaction())
    	        {
		        	try
	    	    	{
	                    model = new ${classObject.getName()}();
	                    model.copy( session.Get<${className}>(pk.getFirstKey()) );
					}
					catch( Exception exc )
					{
						model = null;
						Console.WriteLine("Exception caught: {0}", exc);
						throw ( new ProcessingException( "${className}DAO.find${className} failed for primary key " + pk + " - " + exc.ToString() ) );		
					}		
					finally
					{
					}
				}
			}		    
					
        	return( model );
    	}
	    
#if ( $aib.hasIdentity( $classObject ) == true )		
        ///<summary>
        /// returns a List of all the ${className} entities
        /// <returns></returns>
        ///</summary>
	    public List<${className}> findAll${className}()
	    {
			List<${className}> refList = new List<${className}>();
			IList list;

     		using (ISession session = FrameworkPersistenceHelper.OpenSession())
     		{
	            using (ITransaction transaction = session.BeginTransaction())
    	        {
					try
					{
						string buf 	= "from ${className}";
				 		IQuery query = session.CreateQuery( buf );
				 		
						if ( query != null )
						{
							list = query.List();
							${className} model = null;
							
			                foreach (${className} listEntry in list)
			                {
			                    model = new ${className}();
			                    model.copyShallow(listEntry);
			                    refList.Add(model);
			                }
						}
					}
					catch( Exception exc )
					{
						Console.WriteLine("Exception caught: {0}", exc);
						throw ( new ProcessingException( "${className}DAO.findAll${className} failed - " + exc.ToString() ) );		
					}		
					finally
					{
					}
				}
			}
			if ( refList.Count <= 0 )
			{
				logger.LogInformation( "${className}DAO:findAll${className}s() - List is empty.");
			}
	        
			return( refList );		        
	    }
		
        ///<summary>
        /// Inserts a new ${classObject.Name} model into the persistent store and returns 
        /// <param name="model"></para>
        /// <returns></returns>
        ///</summary>
	    public ${className} create${className}( ${className} model )
	    {
     		using (ISession session = FrameworkPersistenceHelper.OpenSession())
     		{
	            using (ITransaction transaction = session.BeginTransaction())
    	        {
			    	try
			    	{
			    		if ( model != null )
			    		{
		    	        	session.Save(model);
		        	    	transaction.Commit();
		        	    }
		        	    else
						{
							string errMsg = "${className}DAO.create${className} - null model provided but not allowed";
							logger.LogInformation( errMsg );
							throw ( new ProcessingException( errMsg ) );		
						}		        	    
					}
					catch( Exception exc )
					{
						string errMsg = "${className}DAO.create${className} - Hibernate failed to rollback - " + exc.ToString();
						Console.WriteLine("Exception caught: {0}", exc.ToString());
						logger.LogInformation( errMsg );
						throw ( new ProcessingException( errMsg ) );		
					}		
					finally
					{
					}		    
			        return( model );
				}
			}	 
	    }
		    
        ///<summary>
        /// Updates the provided ${className} model to the persistent store.
        /// <param name="model"></para>
        /// <returns></returns>
        ///</summary>
	    public ${className} save${className}( ${className} model )
	    {
     		using (ISession session = FrameworkPersistenceHelper.OpenSession())
     		{
	            using (ITransaction transaction = session.BeginTransaction())
    	        {
			    	try
			    	{
		    	        session.Update(model);
		        	    transaction.Commit();
					}
					catch( Exception exc )
					{
						string errMsg = "${className}DAO.save${className} - Hibernate failed to rollback - " + exc.ToString();
						Console.WriteLine("Exception caught: {0}", exc.ToString());
						logger.LogInformation( errMsg );
						throw ( new ProcessingException( errMsg ) );		
					}		
					finally
					{
					}		    
			        return( model );
				}
			}	 
	    }
	    
        ///<summary>
        /// Removes the associated ${className} model from the persistent store.
        /// <param name="pk"></para>
        /// <returns></returns>
        ///</summary>
	    public bool delete${className}( ${className}PrimaryKey pk ) 
	    {
	    	bool deleted = false;
	    	
			using (ISession session = FrameworkPersistenceHelper.OpenSession())
			{
                using (ITransaction transaction = session.BeginTransaction())
                {
			    	try
	    			{
	    				${className} model = find${className}(pk);    	
	    			
	    				session.Delete( model );
                    	transaction.Commit();
                    	deleted = true;
                	}	    
					catch( Exception exc )
					{
						Console.WriteLine("Exception caught: {0}", exc );
						logger.LogInformation( "${className}DAO.delete${className} failed - " + exc.ToString() );
						throw ( new ProcessingException( "${className}DAO.delete${className} failed - " + exc.ToString() ) );					
					}		
					finally
					{
					}
				}	    			    	
			}
			
			return deleted;
	    }

#end ##if ( $aib.hasIdentity( $classObject ) == true )		

//*****************************************************
// Attributes
//*****************************************************
		private readonly ILogger<${className}DAO> logger;
	}

}


