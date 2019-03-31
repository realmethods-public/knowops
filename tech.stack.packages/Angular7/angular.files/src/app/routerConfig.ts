// routerConfig.ts

import { Routes } from '@angular/router';
#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
import { Create${className}Component } from './components/${className}/create/create.component';
import { Edit${className}Component } from './components/${className}/edit/edit.component';
import { Index${className}Component } from './components/${className}/index/index.component';
#end##foreach( $class in $aib.getClassesToGenerate() )

#foreach( $class in $aib.getClassesToGenerate() )
#set( $className = $class.getName() )
export const ${className}Routes: Routes = [
  { path: 'create${className}',
    component: Create${className}Component
  },
  {
    path: 'edit${className}/:id',
    component: Edit${className}Component
  },
  { path: 'index${className}',
    component: Index${className}Component
  }
];
#end##foreach( $class in $aib.getClassesToGenerate() )
