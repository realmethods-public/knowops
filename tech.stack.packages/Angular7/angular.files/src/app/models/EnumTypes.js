#foreach ( $enum in $aib.getEnumClassesToGenerate() )
#set( $enumName = $enum.getName() )

// enum type ${enumName}
export let $enumName = {
#set( $attributes = $enum.getAttributes() )
#foreach ( $attribute in $attributes )
	${attribute.getName()}:"${attribute.getName()}",
#end##foreach ( $attribute in $attributes )
}
#end##foreach ( $enum in $aib.getEnumClassesToGenerate() )
