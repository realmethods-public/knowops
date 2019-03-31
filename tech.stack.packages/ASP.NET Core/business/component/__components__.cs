#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )

/**
#if( $classObject.hasDocumentation() == false )
 * Encapsulates data for business entity ${classObject.getName()}.
#else
 * $classObject.getDocumentation()
#end 
 * 
 * @author $aib.getAuthor()
 */
namespace ${appName}.Components
{
// AIB : \#getClassDecl()
# getClassDecl()
         // ~AIB

         //************************************************************************
         // Constructors
         //************************************************************************

        /// <summary>
        /// Default constructor, using dependency injection to acquire a ILogger<${className}> implementation
        /// </summary>
        /// <param name="_logger"></param>
        public ${className}Component( ( ILogger<${className}> _logger )
		{
			logger = _logger;
		}

//************************************************************************
// Business Methods
//************************************************************************
// AIB : \#getBusinessMethodImplementations( \$classObject.getName() \$classObject \$classObject.getBusinessMethods() \$classObject.getInterfaces() \$aib.getProxyTarget() true \$aib.usingSOAP() \$aib.usingCMP() )
#getBusinessMethodImplementations( $classObject.getName() $classObject $classObject.getBusinessMethods() $classObject.getInterfaces() $aib.getProxyTarget() true $aib.usingSOAP() $aib.usingCMP() )
// ~AIB
	
//************************************************************************
// Protected / Private Methods
//************************************************************************
    
//************************************************************************
// Attributes
//************************************************************************

// AIB : \#getAttributeDeclarations( true  )
#getAttributeDeclarations( true )
// ~AIB

	private readonly ILogger<${className}> logger;


}


