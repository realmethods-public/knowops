#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ${className}Service } from '../../../services/${className}.service';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';
import { SubBaseComponent } from '../../${className}/sub.base.component';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css']
})
export class Edit${className}Component extends SubBaseComponent implements OnInit {

  ${lowercaseClassName}: any;
  ${lowercaseClassName}Form: FormGroup;
  title = 'Edit ${className}';
  
  constructor( http: HttpClient, 
  				private route: ActivatedRoute, 
  				private router: Router, 
  				private service: ${className}Service, 
  				private fb: FormBuilder) {
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
  update${className}(${argsAsString}) {
    this.route.params.subscribe(params => {
#if ( $argsAsString == $null || $argsAsString == "" )
    	this.service.update${className}(params['id'])
#else
    	this.service.update${className}(${argsAsString}, params['id'])
#end##if ( $argsAsString == $null || $argsAsString == "" )
      		.then(success => this.router.navigate(['/index${className}']) );
  });
}

// initialization
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.${lowercaseClassName} = this.service.edit${className}(params['id']).subscribe(res => {
        this.${lowercaseClassName} = res;
      });
    });
    
    super.ngOnInit();
  }
}
