#header()
#set( $appName = $aib.getApplicationNameFormatted() )
using System;
using System.Collections;

using NHibernate;
using NHibernate.Cfg;
using NHibernate.Tool.hbm2ddl;

using ${appName}.Exceptions;
using System.IO;

namespace ${appName}.Persistence
{
    /// <summary>
    /// Start up methodology for getting nHibernate started automatically via a static constructor
    /// </summary>
    public class FrameworkPersistenceHelper
    {
        /// <summary>
        /// factory singleton pattern of the nHibernate Session Factory interface
        /// </summary>
        private static ISessionFactory _sessionFactory;

        /// <summary>
        /// get method for the Session Factory interface singleton
        /// <return></return>
        /// </summary>
        private static ISessionFactory SessionFactory
        {
            get
            {
                if (_sessionFactory == null)
                {
                    try
                    {
                        var configuration = new Configuration();
                        //configuration.Configure();
                        configuration.Configure(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "hibernate.cfg.xml"));
#foreach( $class in $aib.getClassesWithIdentity() )
#if ( $class.hasParent() == false )
                        configuration.AddFile("${class.getName()}.hbm.xml");
#end
#end
						new SchemaUpdate(configuration).Execute(false, true);
                        //new SchemaExport(configuration).Execute(true, true, false);

                        _sessionFactory = configuration.BuildSessionFactory();
                    }
                    catch( Exception exc )
                    {
                        Console.WriteLine("FrameworkPersistenceHelper.SessionFactory() " + exc.ToString());
                    }
                }
                return _sessionFactory;
            }
        }

        /// <summary>
        /// open/start the hibernate session using the Session Factory singleton interface
        /// <return></return>
        /// </summary>
        public static ISession OpenSession()
        {
            return SessionFactory.OpenSession();
        }

    }
}