import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule, MatDatepickerModule, MatCheckboxModule, MatButtonModule, MatFormFieldModule, MatSelectModule } from '@angular/material';
import { MatMomentDateModule } from "@angular/material-moment-adapter";
import {MatMenuModule} from '@angular/material/menu';
import {MatToolbarModule} from '@angular/material/toolbar';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { MatSidenavModule } from '@angular/material/sidenav'


#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { Index${className}Component } from './components/${className}/index/index.component';
import { Create${className}Component } from './components/${className}/create/create.component';
import { Edit${className}Component } from './components/${className}/edit/edit.component';
#end##foreach( $class in $aib.getClassesToGenerate() )

import * as appRoutes from './routerConfig';

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { ${className}Service } from './services/${className}.service';
#end##foreach( $class in $aib.getClassesToGenerate() )

@NgModule({
  declarations: [
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
    Index${className}Component,
    Create${className}Component,
    Edit${className}Component,
#end##foreach( $class in $aib.getClassesToGenerate() )    
    AppComponent
  ],
  imports: [

    BrowserModule, 
    NgbModule,
    MatMenuModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
	MatMomentDateModule,
    BrowserAnimationsModule,
	HttpClientModule, 
    ReactiveFormsModule,
    FormsModule,
    MatSidenavModule,    
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )    
    RouterModule.forRoot(appRoutes.${className}Routes), 
#end##foreach( $class in $aib.getClassesToGenerate() )    
  ],
#set( $numClasses = ${aib.getClassesToGenerate().size()} )
#set( $services = "" )
#foreach( $class in $aib.getClassesToGenerate() )
#set( $services = "${services}${class.getName()}Service" )
#if( $velocityCount < $numClasses )
#set( $services = "${services}," )
#end##if( $numClasses == $velocityCount )
#end##foreach( $class in $aib.getClassesToGenerate() )  
  providers: [$services],
  bootstrap: [AppComponent]
})
export class AppModule { }
