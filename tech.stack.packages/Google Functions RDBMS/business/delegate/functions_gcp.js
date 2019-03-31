#header()
#set( $reqresCall = "${reqresCall} => {" )
#set( $reqresTerminate = "" )
#if (${aib.getParam('gcp-functions.useFirebase')} == "true" )	
const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp(functions.config().firebase)
#set( $reqresCall = "functions.https.onRequest((req, res) =>  {" )
#set( $reqresTerminate = ")" )
#end

##foreach( $class in $aib.getClassesWithIdentity() )
##require('${class.getName()}_gcp.js');
##require('${class.getName()}PrimaryKey_gcp.js');
##end ##foreach( $class in $aib.getClassesWithIdentity() )
/**
 * GCP Function proxy delegate functions.
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
exports.create${className} = ${reqresCall}	

	var businessObject = req.body;
		
	if ( businessObject == null ) {
        var errMsg = "error on create${className} - null ${className} provided but not allowed. ";
        console.log( errMsg );
        throw new Error( errMsg ); 
    }
  
    try {
        innerCreate${className}( businessObject, req, res );
    } catch (err) {
    	var errMsg = "error on create${className} - unable to create ${className}" + getContextDetails(req, err);
    	console.log( errMsg );
        throw new Error( errMsg );
    }     
}${reqresTerminate};

function innerCreate${className}( businessObject, req, res ) {
	
	try {
        var actionName 		= "save";
		callPut("${className}", actionName, businessObject, res);
	} catch (err) {
		console.log( "exception caught in innerCreate${className} - " + getContextDetails(req, err) );
		throw err;
	}
};

/**
 * function to retrieve the ${className} data via a supplied ${className} primary key.
 */
exports.get${className} = ${reqresCall}
    
    var key = 	{ 
    				${pkExpression} : 0
    			};                
	
	key.${pkExpression} = req.body.${pkExpression};
    
    try {
        innerGet${className}( key, req, res );
    }
    catch( err ) {
        var errMsg = "err+or on create${className} - unable to locate ${className} with key " 
        	+ key.toString() + " - " + getContextDetails(req, err);
    	console.log( errMsg );
        throw new Error( errMsg );
    }
}${reqresTerminate};

function innerGet${className}( primaryKey, req, res ) {
	try {
        var actionName	= "load";
		var businessObject	= callGet("${className}", actionName, primaryKey, res );
		return businessObject;
	} catch (err) {
		console.log( "exception caught in innerGet${className} - " + getContextDetails(req, err) );
		throw err;
	}
};

     
/**
 * function to save the provided ${className} data.
 */
exports.save${className} = ${reqresCall}

    var businessObject = req.body;                

	if ( !exists( businessObject ) ) {
        var errMsg = "error on create${className} - null ${className} provided but not allowed.";
        console.log( errMsg );
        throw new Error( errMsg ); 
    }
	
                
    if ( exists( businessObject.${pkExpression} ) ) {
        try {                    
            innerSave${className}(businessObject, req, res);
        }
        catch (err) {
        	var errMsg = "error on create${className} - unable to save ${className} " 
        		+ " - " + getContextDetails(req, err);
    		console.log( errMsg );
        	throw new Error( errMsg );        
        }
    }
    else {
        var errMsg = "Unable to create ${className} due to it having a null ${className}PrimaryKey." 
        				+ " - " + getContextDetails(req, err)
        console.log( errMsg );
        throw new Error( errMsg );
    }
    
}${reqresTerminate};
     
function innerSave${className}( businessObject, req, res ) {
	try {
		var actionName	= "save";
        var businessObject	= callPut("${className}", actionName, businessObject, res);
        return businessObject;
	}
	catch ( err ) {
		console.log( "exception caught in innerSave${className} - " + getContextDetails(req, err) );
		throw err;
	}
};

/**
 * function to retrieve a all ${className}s
 */
exports.getAll${className} = ${reqresCall}

    try {
        var actionName = "viewAll";
        innerGetAll${className}(req, res);  
    } catch( err ) {
    	var errMsg = "error on create${className} - failed to getAll${className} " 
    		+ " - " + getContextDetails(req, err);
		console.log( errMsg );
    	throw new Error( errMsg ); 
    }
}${reqresTerminate};
           
function innerGetAll${className}(req, res) {
	try {
		var actionName = "viewAll";
        callGet("${className}", actionName, null, res);	
	} catch( err ) {
		console.log( "exception caught in innerGetAll${className} - " + getContextDetails(req, err) );
	    throw err;
	}
}; 
     
/**
 * function to deletes the associated ${className} using its provided primary key.
 */
exports.delete${className} = ${reqresCall}

    var key = 	{ 
    				${pkExpression} : 0
    			};                
	
	key.${pkExpression} = req.body.${pkExpression};
	
    try {                    
       	innerDelete${className}(key, req, res);
    } catch (err) {
    	var errMsg = "error on create${className} - Unable to delete ${className} using key = "  
    		+ key 
    		+ " - " + getContextDetails(req, err);
		console.log( errMsg );
    	throw new Error( errMsg ); 
    }
}${reqresTerminate};

function innerDelete${className}( primaryKey, req, res ) {
	try {
		var actionName 	= "delete";
        var result 		= callGet("${className}", actionName, primaryKey, res);
	} catch ( err ) {
		console.log( "exception caught in innerDelete${className} - " + getContextDetails(req, err) );
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
exports.get${roleName}On${className} = ${reqresCall}	
    var keys = 	req.body;
    callGet("${className}", "load${roleName}", keys, res);
}${reqresTerminate};

/**
 * function to assign the ${roleName} on a ${className} using the provided primary key of a ${childName}
 */
exports.save${roleName}On${className} = ${reqresCall}

    var keys = 	req.body;
    callGet("${className}", "save${roleName}", keys, res);
    	
}${reqresTerminate};

/**
 * function to remove the assignment of the ${roleName} on a ${className}
 */
exports.delete${roleName}On${className} = ${reqresCall}

    var keys = 	req.body;
    callGet("${className}", "delete${roleName}", keys, res);

}${reqresTerminate};
		
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
exports.get${roleName}On${className} = ${reqresCall}
    var keys = 	req.body;
    callGet("${className}", "load${roleName}", keys, res);
}${reqresTerminate};
    
/**
 * function to save multiple ${childName} entities as the ${roleName} 
 * of the relevant ${className} associated with the provided primary key
 * @param parentKey
 * @param childKeys
 */
exports.assign${roleName}On${className} = ${reqresCall}

    var keys = 	req.body;
    callGet("${className}", "save${roleName}", keys, res);
	
}${reqresTerminate};

/**
 * function to delete multiple ${childName} entities as the ${roleName} 
 * of the relevant ${className} associated with the provided primary key
 * @param parentKey
 * @param childKeys
 */
exports.delete${roleName}On${className} = ${reqresCall}		

    var keys = 	req.body;
    callGet("${className}", "delete${roleName}", keys, res);
	
}${reqresTerminate};

#end ##foreach( $multiAssociation in $class.getMultipleAssociations() )

#end ##foreach( $class in $aib.getClassesWithIdentity() )
		
/**
 * internal function used to handle HTTP request to the RESTful API
 */		
function callGet( packageName, actionName, data, response ) {
	return( call( packageName, actionName, data, response, 'GET' ) );
}

function callPut( packageName, actionName, data, response ) {
	return( call( packageName, actionName, data, response, 'PUT' ) );
}

function call( packageName, actionName, data, response, method) {
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
      		var responseObject = JSON.parse(responseString);
      		if ( response !== null && response !== 'undefined' )
            	response.status(200).send( responseObject );        
    	});
	});
	
	req.write( dataString );
	req.end();
  		
};

function getContextDetails( request, error ) {
    return( error.message );
};

function exists( objToTest )
{
	if (null == objToTest) 
		return false;
  	if ("undefined" == typeof(objToTest)) 
  		return false;
  	return true;
}

// global vars				   
var errHandler = function(err) {
    console.log(err);
}

var RESTFUL_API_DAO_URL 	= "${aib.getParam('gcp-functions.RESTfulAPIsURL')}";
var PORT 					= ${aib.getParam('gcp-functions.RESTfulAPIsPort')};