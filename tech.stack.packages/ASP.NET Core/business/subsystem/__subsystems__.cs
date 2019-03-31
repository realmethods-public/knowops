#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )

#header()

/**
 */
namespace ${appName}.Subsystems
{
        /// <summary>
#if ( $classObject.hasDocumentation() == false )
        /// Encapsulates data for business entity ${classObject.getName()}.
#else
        /// $classObject.getDocumentation()
#end 
        /// @author $aib.getAuthor()
        /// </summary>
# getClassDecl()

        //************************************************************************
        // Constructors
        //************************************************************************

        ///<summary>
        /// Default constructor, using dependency injection to acquire a ILogger<${className}> interface
        /// <param name="_logger"></para>
        ///</summary>
        public ${className}Subsystem( ( ILogger<${className}> _logger )
		{
			logger = _logger;
		}

//************************************************************************
// Business Methods
//************************************************************************
#getBusinessMethodImplementations( $classObject.getName() $classObject $classObject.getBusinessMethods() $classObject.getInterfaces() $aib.getProxyTarget() true $aib.usingSOAP() $aib.usingCMP() )
	
//************************************************************************
// Protected / Private Methods
//************************************************************************
    
//************************************************************************
// Attributes
//************************************************************************
#getAttributeDeclarations( true )

	private readonly ILogger<${className}> logger;


}


