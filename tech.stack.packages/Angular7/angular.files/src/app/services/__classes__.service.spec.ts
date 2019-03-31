#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { TestBed } from '@angular/core/testing';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

import { ${className}Service } from './${className}.service';

describe('${className}Service', () => {
  	beforeEach(() => {
	  TestBed.configureTestingModule({ imports: [HttpClient, FormGroup, FormBuilder, Validators], providers: [${className}Service] });
	});

  it('should be created', () => {
    const service: ${className}Service = TestBed.get(${className}Service);
    expect(service).toBeTruthy();
  });
});
