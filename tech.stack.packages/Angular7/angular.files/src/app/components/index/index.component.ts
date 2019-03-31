#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { ${className}Service } from '../../../services/${className}.service';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { ${className} } from '../../../models/${className}';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class Index${className}Component implements OnInit {

  ${lowercaseClassName}s: any;

  constructor(private http: HttpClient, 
  				private route: ActivatedRoute, 
  				private router: Router, 
  				private service: ${className}Service) {}

  ngOnInit() {
    this.get${className}s();
  }

  get${className}s() {
    this.service.get${className}s().subscribe(res => {
      this.${lowercaseClassName}s = res;
    });
  }

  delete${className}(id) {
    this.service.delete${className}(id)
		.then(success => 
			{
				this.router.navigateByUrl('/', {skipLocationChange: false}).then(()=>
							this.router.navigate(['index${className}']));
			});  }
}
