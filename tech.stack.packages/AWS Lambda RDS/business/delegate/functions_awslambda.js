'use strict';
#if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" ) 
const AWS = require('aws-sdk');
const uuid = require('uuid');
#end ##if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" ) 
#header() 
/**
 * AWS Lambda Function proxy delegate functions.
 * <p>
 * These functions implement the Business Delegate design pattern for the purpose of:
 * <ol>
 * <li>Reducing coupling between the business tier and a client of the business tier by hiding all business-tier implementation details</li>
 * <li>Improving the availability of related services in the case of a business related service failing.</li>
 * <li>Exposes a simpler, uniform interface to the business tier, making it easy for clients to consume a business data.</li>
 * <li>Hides the communication protocol that may be required to fulfill business related services.</li>
 * <li>Hides the details of how business data is persisted.</li>
 * </ol>
 * <p>
 * @author ${aib.getAuthor()}
 */
#foreach( $class in $aib.getClassesWithIdentity() )

#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter( ${className} )} )
#set( $pk = ${class.getFirstPrimaryKey()} )		
#set( $pkExpression = "${pk.getName()}" ) 
/**
 * function to create the provided ${className} data raising an exception if it fails
 */
exports.create${className} = function(event, context, callback) {	
	var businessObject = event.body;
		
	if ( businessObject == null ) {
        var errMsg = "error on create${className} - null ${className} provided but not allowed. ";
        console.log( errMsg );
        callback( new Error( errMsg ) ); 
    }
  
    try {
        innerCreate${className}( businessObject, event, context, callback);
    } catch (err) {
    	var errMsg = "error on create${className} - unable to create ${className}" + getContextDetails(context, err);
    	console.log( errMsg );
        callback( new Error( errMsg ) );
    }     
};

function innerCreate${className}( businessObject, event, context, callback ) {
	try {
        var actionName 		= "save";
		callPut("${className}", actionName, businessObject, event, callback);
	} catch (err) {
		console.log( "exception caught in innerCreate${className} - " + getContextDetails(context, err) );
		callback( err );
	}
};

/**
 * function to retrieve the ${className} data via a supplied ${className} primary key.
 */
exports.get${className} = function(event, context, callback) {
    var key = 	{ 
    				${pkExpression} : 0
    			};                
	
	key.${pkExpression} = event.${pkExpression};
    
    try {
        innerGet${className}( key, event, context, callback);        
    }
    catch( err ) {
        var errMsg = "err+or on create${className} - unable to locate ${className} with key " 
        	+ key.toString() + " - " + getContextDetails(context, err);
    	console.log( errMsg );
        callback(  new Error( errMsg ) );
    }
};

function innerGet${className}( primaryKey, event, context, callback ) {
	try {
        var actionName	= "load";
		callGet("${className}", actionName, primaryKey, event, callback);
	} catch (err) {
		console.log( "exception caught in innerGet${className} - " + getContextDetails(context, err) );
		throw err;
	}
};

     
/**
 * function to save the provided ${className} data.
 */
exports.save${className} = function(event, context, callback) {
    var businessObject = event.body;                

	if ( !exists( businessObject ) ) {
        var errMsg = "error on create${className} - null ${className} provided but not allowed.";
        console.log( errMsg );
        callback( new Error( errMsg ) ); 
    }
	                
    if ( exists( businessObject.${pkExpression} ) ) {
        try {                    
            innerSave${className}(businessObject, event, context, callback);
        }
        catch (err) {
        	var errMsg = "error on create${className} - unable to save ${className} " 
        		+ " - " + getContextDetails(context, err);
    		console.log( errMsg );
        	callback( new Error( errMsg ) );        
        }
    }
    else {
        var errMsg = "Unable to create ${className} due to it having a null ${className}PrimaryKey." 
        				+ " - " + getContextDetails(context)
        console.log( errMsg );
        callback( new Error( errMsg ) );
    }
    
};
     
function innerSave${className}( businessObject, event, context, callback ) {
	try {
		var actionName	= "save";
        callPut("${className}", actionName, businessObject, event, callback);
	}
	catch ( err ) {
		console.log( "exception caught in innerSave${className} - " + getContextDetails(context, err) );
		throw err;
	}
};

/**
 * function to retrieve a all ${className}s
 */
exports.getAll${className} = function(event, context, callback) {
    try {
        var actionName = "viewAll";
        innerGetAll${className}(event, context, callback);  
    } catch( err ) {
    	var errMsg = "error on create${className} - failed to getAll${className} " 
    		+ " - " + getContextDetails(context, err);
		console.log( errMsg );
    	callback( new Error( errMsg ) ); 
    }
};
           
function innerGetAll${className}(event, context, callback) {
	try {
		var actionName = "viewAll";
        callGet("${className}", actionName, null, event, callback);	
	} catch( err ) {
		console.log( "exception caught in innerGetAll${className} - " + getContextDetails(context, err) );
	    throw err;
	}
}; 
     
/**
 * function to delete the associated ${className} using its provided primary key.
 */
exports.delete${className} = function(event, context, callback) {
    var key = 	{ 
    				${pkExpression} : 0
    			};                
	
	key.${pkExpression} = event.${pkExpression};
	
    try {                    
       	innerDelete${className}(key, event, context, callback);
    } catch (err) {
    	var errMsg = "error on create${className} - Unable to delete ${className} using key = "  
    		+ key 
    		+ " - " + getContextDetails(context, err);
		console.log( errMsg );
    	callback( new Error( errMsg ) ); 
    }
};

function innerDelete${className}( primaryKey, event, context, callback) {
	try {
		var actionName 	= "delete";
        callGet("${className}", actionName, primaryKey, event, callback );
	} catch ( err ) {
		console.log( "exception caught in innerDelete${className} - " + getContextDetails(context, err) );
		throw err;
	}
};

// role related methods
#set( $includeComposites = false )

#foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowerRoleName = ${Utils.lowercaseFirstLetter( ${roleName} )} )
#set( $childName = $singleAssociation.getType() )
#set( $parentName  = $class.getName() )
/**
 * function to get the ${roleName} using the provided primary key of a ${className}
 */
exports.get${roleName}On${className} = function(event, context, callback) {	
    var keys = 	event.body;
    callGet("${className}", "load${roleName}", keys, event, callback );
};

/**
 * function to assign the ${roleName} on a ${className} using the provided primary key of a ${childName}
 */
exports.save${roleName}On${className} = function(event, context, callback) {
    var keys = 	event.body;
    callGet("${className}", "save${roleName}", keys, event, callback);
};

/**
 * function to remove the assignment of the ${roleName} on a ${className}
 */
exports.delete${roleName}On${className} = function(event, context, callback) {
    var keys = 	event.body;
    callGet("${className}", "delete${roleName}", keys, event, callback);
};
		
#end ##foreach( $singleAssociation in $class.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $class.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowerRoleName = ${Utils.lowercaseFirstLetter( ${roleName} )} )
#set( $childName = $multiAssociation.getType() )
#set( $parentName  = $class.getName() )
/**
 * function to retrieve the ${roleName} on a ${className}
 * @param parentKey
 */
exports.get${roleName}On${className} = function(event, context, callback) {
    var keys = 	event.body;
    callGet("${className}", "load${roleName}", keys, event, callback);
};
    
/**
 * function to save multiple ${childName} entities as the ${roleName} 
 * of the relevant ${className} associated with the provided primary key
 * @param parentKey
 * @param childKeys
 */
exports.assign${roleName}On${className} = function(event, context, callback) {
    var keys = 	event.body;
    callGet("${className}", "save${roleName}", keys, event, callback);
};

/**
 * function to delete multiple ${childName} entities as the ${roleName} 
 * of the relevant ${className} associated with the provided primary key
 * @param parentKey
 * @param childKeys
 */
exports.delete${roleName}On${className} = function(event, context, callback) {		
    var keys = 	event.body;
    callGet("${className}", "delete${roleName}", keys, event, callback);
};

#end ##foreach( $multiAssociation in $class.getMultipleAssociations() )

#end ##foreach( $class in $aib.getClassesWithIdentity() )
		
/**
 * internal function used to handle HTTP request to the RESTful API
 */		
function callGet( packageName, actionName, data, event, callback ) {
	return( call( packageName, actionName, data, event, callback,  'GET' ) );
};

function callPut( packageName, actionName, data, event, callback ) {
	return( call( packageName, actionName, data, event, callback, 'PUT' ) );
};

function call( packageName, actionName, data, event, callback, method) {
	var querystring	= require('querystring');
	var https 		= require('http');
	var host 		= RESTFUL_API_DAO_URL;
	var endPoint 	= "/" + packageName + "/" + actionName;
  	var dataString 	= JSON.stringify(data);
	var headers 	= {};
	
	console.log("datastring is " + dataString );
	
	if (method == 'GET') {
		endPoint += '?' + querystring.stringify(data);
	}
	else {
		endPoint += '?' + dataString;	
		headers = {
			'Content-Type': 'application/json',
			'Content-Length': dataString.length
		};
	}
		
  	var options = {
  		hostname: host,
    	path: endPoint,
    	method: "GET",
    	port: PORT,
    	headers: headers
  	};

  	console.log( options );
	var responseString = '';
	var req = https.request(options, function(res) {
    	res.setEncoding('utf-8');
    	res.on('data', function(data) {
      		responseString += data;
    	});
       	res.on('end', function() {    
#if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" )  
			if ( exists( KINESIS_STREAM_NAME ) == true )
			{
				const kinesis = new AWS.Kinesis();
	  			const partitionKey = uuid.v1();
			  	const params = {
			    	Data: 	JSON.stringify({
			    				entityName : packageName,
			    				action : actionName,
			    				payload : data,
			    				response : JSON.parse(responseString)
			    			}),
#if ( ${aib.getParam('aws-lambda.append class name to stream name')} == "true" )		    	
			    	StreamName: KINESIS_STREAM_NAME + packageName,		    	
#else
					StreamName: KINESIS_STREAM_NAME,
#end ##if ( ${aib.getParam('aws-lambda.append class name to stream name')} == "true" )
			    	PartitionKey: partitionKey
			  	};
	  			return kinesis.putRecord(params, (error, data) => {
	    			if (error) {
	      				callback(error);
	    			}
	    			
	       			callback(null, JSON.parse(responseString));
	  			});
	  		}
#else
	    	callback(null, JSON.parse(responseString));	
#end ##if ( ${aib.getParam('aws-lambda.use kinesis')} == "true" )
    	});
	});
	
	req.write( dataString );
	req.end();
  		
};

function getContextDetails( context, error ) {
	var errMsg = '';
	if ( exists( context ) ) {
		errMsg = errMsg + 'remaining time = ' + context.getRemainingTimeInMillis()
	    				+ ', functionName = ' +  context.functionName
	    				+ ', AWSrequestID = ' + context.awsRequestId
	    				+ ', logGroupName = ' + context.log_group_name
	    				+ ', logStreamName = ' + context.log_stream_name
	    				+ ', clientContext = ' + context.clientContext;
	    if ( exists( context.identity ) ) 
	        errMsg = errMsg + ', Cognito identity ID = ' + context.identity.cognitoIdentityId;
	}
		        	
	if ( exits( error ) )
		errMsg = errMsg + ', ' + error.message;
		
    return( errMsg );
};

function exists( objToTest ) {
	if (null == objToTest) 
		return false;
  	if ("undefined" == typeof(objToTest)) 
  		return false;
  	return true;
};

// global vars				   
var errHandler = function(err) {
    console.log(err);
};

var RESTFUL_API_DAO_URL 	= process.env.delegateDAOHost;
var PORT 					= process.env.delegateDAOPort;
var KINESIS_STREAM_NAME		= process.env.kinesisStreamName;