#header()
package ${aib.getRootPackageName()}.#getDAOPackageName();

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.Datastore;

import ${aib.getRootPackageName()}.#getBOPackageName().*;
import ${aib.getRootPackageName()}.exception.*;


/** 
 * Base class for all application MongoDB Data Access Objects.
 *
 * @author $aib.getAuthor()
 */
public abstract class BaseDAO
{
	public void done()
	{
	}

	/**
	 * Singleton factory pattern, create a MongoClient by reading in the mongodb.server addresses option
	 * to create a client to either a single instance or a cluster.  Also applies authentication using
	 * the option parameter mondb.credentials (ex: mongodb.credential="user1:pw1,user2:pw2,....userN:pwN"
	 * @return
	 */
	protected Datastore getDatastore()
	{
		if ( dataStore == null )
		{
			final Morphia morphia;
			final List<com.mongodb.ServerAddress> servers	= new ArrayList();
			final List<MongoCredential> credentials 		= new ArrayList<MongoCredential>();			
			final String serverAddress 						= MONGO_SERVER_ADDRESSES;
			final String credentialInputs					= MONGO_CREDENTIALS;
			StringTokenizer itemTokenizer 					= null;
			int port										= -1;
			
			if ( DEFAULT_HOST_ADDRESS != null ) {
				port = new Integer( DEFAULT_HOST_PORT).intValue();
				servers.add( new ServerAddress( DEFAULT_HOST_ADDRESS, port ) );
			}
			else if ( !serverAddress.isEmpty() )
			{
				StringTokenizer tokenizer 	= new StringTokenizer( serverAddress, "," );
				String hostName 			= null;
				
				while( tokenizer.hasMoreTokens() )
				{
					itemTokenizer = new StringTokenizer( tokenizer.nextToken(), ":" );
					
					if ( itemTokenizer.hasMoreTokens() )
					{
						hostName = itemTokenizer.nextToken();
						if ( itemTokenizer.hasMoreTokens() )
						{ 
							port = new Integer( itemTokenizer.nextToken() ).intValue();
							servers.add( new ServerAddress( hostName, port ) );
						}
					}
				}
			}
			
			if ( credentialInputs != null && credentialInputs.length() > 0 )
			{
				StringTokenizer credentialTokenizer = new StringTokenizer( credentialInputs, "," );
				String userId						= null;
				String password						= null;
#set( $dbName = ${aib.getParam( "mongodb.database name" )} )
#if ( $dbName.length() > 0)
				String dbName						= "${dbName}";
#else
				String dbName						= getCollectionName(); // each child entity gets its own db
#end ##if ( $dbName.length() > 0)
	
				while( credentialTokenizer.hasMoreTokens() )
				{
					itemTokenizer = new StringTokenizer( credentialTokenizer.nextToken(), ":" );
					
					if ( itemTokenizer.hasMoreTokens() )
					{
						userId = itemTokenizer.nextToken();
						if ( itemTokenizer.hasMoreTokens() )
						{ 
							password = itemTokenizer.nextToken();
							credentials.add( MongoCredential
												.createPlainCredential(userId, dbName, password.toCharArray()));
						}
					}
				}
			}
            			
			mongoClient = new MongoClient( servers, credentials );
			
			morphia = new Morphia();
			final boolean ignoreInvalidClasses = true;
			dataStore = morphia
						.map(
#set( $classCount = $aib.getClassesWithIdentity().size() )
#foreach( $class in $aib.getClassesWithIdentity() )
  #if( $velocityCount < $classCount )
  	${class.getName()}.class,
  #else
  	${class.getName()}.class
  #end ##if( $velocityCount < $classCount )
#end ##foreach( $class in $aib.getClassesWithIdentity() )
						)
						.createDatastore( mongoClient, "${aib.getApplicationName()}" );
			
			dataStore.ensureIndexes();
			dataStore.ensureCaps();
		}
		
		return( dataStore );
	}

	protected MongoCollection<Document>  getCounterCollection( )
	{
		if ( counterCollection == null )
			counterCollection = getDatabase().getCollection( getCollectionName() + "_counter" );
		
		if ( counterCollection.count() == 0 )
          	counterCollection = createCounterCollection( counterCollection );
  		
		return( counterCollection );
	}

	protected Long getNextSequence( String name) 
	{
	    Document filterQuery 	= new Document( MONGO_ID_FIELD_NAME, name);
	    Document increase 		= new Document( MONGO_SEQ_FIELD_NAME, 1 );
	    Document updateQuery 	= new Document("$inc", increase);
	    Long sequence			= getCounterCollection()
	    							.findOneAndUpdate( filterQuery, updateQuery)
	    							.getLong( MONGO_SEQ_FIELD_NAME );  
	    return ( sequence );
	}	

	protected MongoClient getMongoClient()
	{
		return( mongoClient );
	}
	
	protected MongoDatabase getDatabase()
	{
		return( getDatabase( "${aib.getApplicationName()}" ) );
	}
	
	protected MongoDatabase getDatabase( String dbName )
	{
		return( getMongoClient().getDatabase ( dbName ) );
	}
	
	protected String getCollectionName()
    { 
		String name = "${aib.getParam( "mongodb.default collection name" )}";
		return ( name ); 
	}

	// must overrides	
	protected abstract MongoCollection<Document> createCounterCollection( MongoCollection<Document> collection );
	
// attributes
	private MongoClient mongoClient;
	private Datastore dataStore							= null;
	private MongoCollection<Document> counterCollection	= null;	 
    private static final String DEFAULT_HOST_ADDRESS	= System.getenv("DEFAULT_HOST_ADDRESS");
	private static final String DEFAULT_HOST_PORT		= System.getenv("DEFAULT_HOST_PORT");
	private static final Logger LOGGER 	= Logger.getLogger(BaseDAO.class.getName());
	protected static final String MONGO_ID_FIELD_NAME	= "${aib.getParam( "mongodb.auto-increment id name" )}"; 
	protected static final String MONGO_SEQ_FIELD_NAME	= "${aib.getParam( "mongodb.auto-increment seq name" )}";
	protected final static String MONGO_SERVER_ADDRESSES = java.lang.System.getenv("mongoDbServerAddresses");
	protected final static String MONGO_CREDENTIALS 	= java.lang.System.getenv("mongoDbCredentials");
}
