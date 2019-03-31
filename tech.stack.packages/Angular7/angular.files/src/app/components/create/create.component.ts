#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ${className}Service } from '../../../services/${className}.service';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';
import { BaseComponent } from '../../base.component';
import { SubBaseComponent } from '../../${className}/sub.base.component';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class Create${className}Component extends SubBaseComponent implements OnInit {

  title = 'Add ${className}';
  ${lowercaseClassName}Form: FormGroup;
  
  constructor(  http: HttpClient,
  				private ${lowercaseClassName}service: ${className}Service, 
  				private fb: FormBuilder, 
  				private router: Router) {
	super(http);
    this.createForm();
   }
  createForm() {
    this.${lowercaseClassName}Form = this.fb.group({
#outputDataValidators()    
   });
  }
#set( $includePrimaryKeys = false )
#set( $includeType = false )
#set( $includeAssociations = true )
#set( $delim = ", " )
#set( $suffix = "" )
#set( $argsAsString = ${classObject.getAttributesAsString( ${includePrimaryKeys}, ${includeType}, ${includeAssociations}, ${delim}, ${suffix} )} )        	
  add${className}(${argsAsString}) {
      this.${lowercaseClassName}service.add${className}(${argsAsString})
      	.then(success => this.router.navigate(['/index${className}']) );
  }
  
// initialization  
  ngOnInit() {
  	super.ngOnInit();
  }
}
