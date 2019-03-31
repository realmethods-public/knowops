import { HttpClient } from '@angular/common/http';
import { BaseComponent } from '../base.component';

/** 
	Base class of all $className Edit and Create Components.  
**/
export class SubBaseComponent extends BaseComponent {

  constructor (http: HttpClient) { super(http); }
  
  ngOnInit() {
  	super.ngOnInit();
  	
#foreach ( $association in $classObject.getAssociations() )
	this.init${association.getType()}List();
#end##foreach ( $association in $classObject.getAssociations() )
  }
}
