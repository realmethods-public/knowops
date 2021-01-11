import { HttpClient } from '@angular/common/http';
import * as enumTypes from '../models/EnumTypes';

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
import {${className}Service} from '../services/${className}.service';
#end##foreach( $class in $aib.getClassesToGenerate() )

/** 
	Base class of all Components.  
	For convenience, contains all enums and entity lists 
**/
export class BaseComponent {

    constructor (private http: HttpClient) {}

// enum instances
#foreach ( $enum in $aib.getEnumClassesToGenerate() )
#set( $enumName = $enum.getName() )
#set( $lowercaseEnumName = ${Utils.lowercaseFirstLetter( ${enumName})} )
    ${enumName}s = Object.keys(enumTypes.${enumName});
#end##foreach ( $enum in $aib.getEnumClassesToGenerate() )  

// all collection instances
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
    ${lowercaseClassName}s : any;
#end##foreach( $class in $aib.getClassesToGenerate() )  
  
// initialization  
    ngOnInit() {
    }

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
    init${className}List() {
        if ( this.${lowercaseClassName}s == null ) {
            new ${className}Service(this.http).get${className}s().subscribe(res => {
                this.${lowercaseClassName}s = res;
            });
        }
    }
    
#end##foreach( $class in $aib.getClassesToGenerate() )  
    
// comparison function for select controls  
    compareFn(user1: any, user2: any) {
        return user1 == user2
    }    
}
