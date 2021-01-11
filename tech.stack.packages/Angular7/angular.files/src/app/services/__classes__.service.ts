#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { Injectable } from '@angular/core';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import {${classObject.getName()}} from '../models/${classObject.getName()}';
#foreach( $type in $classObject.getAssociationTypes( $className ) )
import {${type}Service} from '../services/${type}.service';
#end
import { HelperBaseService } from './helperbase.service';

@Injectable({
	providedIn: 'root'
  })
    
export class ${className}Service extends HelperBaseService {

	//********************************************************************
	// general holder 
	//********************************************************************
	${lowercaseClassName} : any;
	
	//********************************************************************
	// Catch all for the return value of a service call
	//********************************************************************
	result: any;

	//********************************************************************
	// sole constructor, injected with the HttpClient
	//********************************************************************
 	constructor(private http: HttpClient) {
 	    super();
    }
 	
#set( $includePrimaryKeys = false )
#set( $includeType = false )
#set( $includeAssociations = true )
#set( $delim = ", " )
#set( $suffix = "" )
#set( $argsAsString = ${classObject.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )
	//********************************************************************
	// add a ${className} 
	// returns the results untouched as a JSON representation 
	// delegates via URI to an ORM handler
	//********************************************************************
  	add${className}(${argsAsString}) : Promise<any> {
    	const uri = this.ormUrl + '/${className}/add';
    	const obj = {
#attributeStructDecl(${classObject})
    	};
    	
    	return this.http.post(uri, obj).toPromise();
  	}

	//********************************************************************
	// gets all ${className} 
	// returns the results untouched as JSON representation of an
	// array of $className models
	// delegates via URI to an ORM handler
	//********************************************************************
	get${className}s() {
    	const uri = this.ormUrl + '/${className}';
    	
    	return this
            	.http.get(uri).map(res => {
              						return res;
            					});
  	}

	//********************************************************************
	// edit a ${className} 
	// returns the results untouched as a JSON representation of a
	// $className model
	// delegates via URI to an ORM handler
	//********************************************************************
  	edit${className}(id) {
    	const uri = this.ormUrl + '/${className}/edit/' + id;
    	
    	return this.http.get(uri).map(res => {
              							return res;
            						});
  	}

	//********************************************************************
	// update a ${className} 
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************
#if ( $argsAsString == $null || $argsAsString == "" )
    update${className}(id)  : Promise<any>  {
#else
	update${className}(${argsAsString}, id)  : Promise<any>  {
#end##if ( ${argAsString}.length() > 0 )
    	const uri = this.ormUrl + '/${className}/update/' + id;
    	const obj = {
#attributeStructDecl(${classObject})
    	};
    	
    	return this.http.post(uri, obj).toPromise();
  	}

	//********************************************************************
	// delete a ${className} 
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************
	delete${className}(id)  : Promise<any> {
    	const uri = this.ormUrl + '/${className}/delete/' + id;

        return this.http.get(uri).toPromise();
  }
  
    	#set( $includeComposites = false )
#foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )
#set( $roleName = $singleAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $singleAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	//********************************************************************
	// assigns a ${roleName} on a ${className}
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************
	assign${roleName}( ${lowercaseClassName}Id, _${lowercaseRoleName}Id ): Promise<any> {

		// get the ${className} from storage
		this.loadHelper( ${lowercaseClassName}Id );
		
		// get the ${childName} from storage
		var tmp 	= new ${childName}Service(this.http).edit${childName}(_${lowercaseRoleName}Id);
		
		// assign the $roleName		
		this.${lowercaseClassName}.$lowercaseRoleName = tmp;
      		
		// save the ${className}
		return this.saveHelper();		
	}

	//********************************************************************
	// unassigns a ${roleName} on a ${className}
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************				
	unassign${roleName}( ${lowercaseClassName}Id ): Promise<any> {

		// get the ${className} from storage
        this.loadHelper( ${lowercaseClassName}Id );
		
		// assign $roleName to null		
		this.${lowercaseClassName}.$lowercaseRoleName = null;
      		
		// save the ${className}
		return this.saveHelper();
	}
	
#end##foreach( $singleAssociation in $classObject.getSingleAssociations( ${includeComposites} ) )

#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
#set( $roleName = $multiAssociation.getRoleName() )
#set( $lowercaseRoleName = ${Utils.lowercaseFirstLetter(${roleName})} )
#set( $childName = $multiAssociation.getType() )
#set( $lowercaseChildName = ${Utils.lowercaseFirstLetter(${childName})} )
#set( $parentName  = $classObject.getName() )
	//********************************************************************
	// adds one or more ${lowercaseRoleName}Ids as a ${roleName} 
	// to a ${className}
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************				
	add${roleName}( ${lowercaseClassName}Id, ${lowercaseRoleName}Ids ): Promise<any> {

		// get the ${className}
		this.loadHelper( ${lowercaseClassName}Id );
				
		// split on a comma with no spaces
		var idList = ${lowercaseRoleName}Ids.split(',')

		// iterate over array of ${lowercaseRoleName} ids
		idList.forEach(function (id) {
			// read the ${childName}		
			var $lowercaseChildName = new ${childName}Service(this.http).edit${childName}(id);	
			// add the ${childName} if not already assigned
			if ( this.${lowercaseClassName}.${lowercaseRoleName}.indexOf(${lowercaseChildName}) == -1 )
				this.${lowercaseClassName}.${lowercaseRoleName}.push(${lowercaseChildName});
		});
				
		// save it		
		return this.saveHelper();
	}			
	
	//********************************************************************
	// removes one or more ${lowercaseRoleName}Ids as a ${roleName} 
	// from a ${className}
	// returns a Promise
	// delegates via URI to an ORM handler
	//********************************************************************						
	remove${roleName}( ${lowercaseClassName}Id, ${lowercaseRoleName}Ids ): Promise<any> {
		
		// get the ${className}
		this.loadHelper( ${lowercaseClassName}Id );

				
		// split on a comma with no spaces
		var idList 					= ${lowercaseRoleName}Ids.split(',');
		var ${lowercaseRoleName} 	= this.${lowercaseClassName}.${lowercaseRoleName};
		
		if ( ${lowercaseRoleName} != null && ${lowercaseRoleName}Ids != null ) {
		
			// iterate over array of ${lowercaseRoleName} ids
			${lowercaseRoleName}.forEach(function (obj) {				
				if ( ${lowercaseRoleName}Ids.indexOf(obj._id) > -1 ) {
					 // remove the ${childName}
					this.${lowercaseClassName}.${lowercaseRoleName}.pop(obj);
				}
			});
					
		    // save it		
			return this.saveHelper();
		}
	}
			
#end##foreach( $multiAssociation in $classObject.getMultipleAssociations() )

	//********************************************************************
	// saveHelper - internal helper to save a ${className}
	//********************************************************************
	saveHelper() : Promise<any> {
		
		const uri = this.ormUrl + '/${className}/update/' + this.${lowercaseClassName}._id;		
		
    	return this
      			.http
      			.post(uri, this.${lowercaseClassName})
				.toPromise();			
	}

	//********************************************************************
	// loadHelper - internal helper to load a ${className}
	//********************************************************************	
	loadHelper( id ) {
		this.edit${className}(id)
        		.subscribe(res => {
        			this.${lowercaseClassName} = res;
      			});
	}
}