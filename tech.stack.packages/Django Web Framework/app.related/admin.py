from django.contrib import admin

# Register your models here.
#foreach( $class in $aib.getClassesToGenerate() )
from .models.${class.getName()} import ${class.getName()}
#end

# Need to add this for each model that requires managing

#foreach( $class in $aib.getClassesToGenerate() )
admin.site.register(${class.getName()})
#end