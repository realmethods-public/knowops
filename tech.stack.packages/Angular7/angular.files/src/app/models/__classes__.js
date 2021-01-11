#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

// Define collection and schema for ${className}
var $className = new Schema({
#set( $includePrimaryKeys = false )
#set( $attributes = ${classObject.getAttributesOrderedInHierarchy( $includePrimaryKeys )} )
#foreach( $attribute in $attributes )  
  ${attribute.getName()}: {
#set( $outputTheAttributeType = true )
#attributeTypeDeclaration(${attribute}, ${classObject}, ${outputTheAttributeType})
  },
#end##foreach ( $attribute in $classObject.getAttributesOrderedInHierarchy( $includePrimaryKeys ) )  
},{
    collection: '${lowercaseClassName}s'
});

module.exports = mongoose.model('$className', $className);