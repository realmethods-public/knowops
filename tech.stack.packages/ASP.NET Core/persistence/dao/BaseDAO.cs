#header()
#set( $appName = $aib.getApplicationNameFormatted() )
#set( $className = $classObject.getName() )
using System;

namespace ${appName}.DAOs
{
    /// <summary>
    /// abstract base class for all Data Access objects, responsible for interacting
    /// with the ORM abstraction to create, read, update, and delete an entity along
    /// with its associated entities
    /// </summary>
	public abstract class BaseDAO
	{}
}
