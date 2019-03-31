#set( $appName = $aib.getApplicationNameFormatted() )
#set( $description = $aib.getParam("application.description") )
#set( $version = $aib.getParam("application.version") )
#set( $author = $aib.getParam("application.author") )
#set( $email = $aib.getParam("application.email") )
#set( $license = $aib.getParam("application.license") )
#set( $url = $aib.getParam("application.url") )
import os
from setuptools import find_packages, setup

with open(os.path.join(os.path.dirname(__file__), 'README.rst')) as readme:
    README = readme.read()

# allow setup.py to be run from any path
os.chdir(os.path.normpath(os.path.join(os.path.abspath(__file__), os.pardir)))

setup(
    name='${appName}',
    version='${version}',
    packages=find_packages(),
    include_package_data=True,
    license='${license}',  # example license
    description='${description}',
    long_description=README,
    url='${url}',
    author='${author}',
    author_email='email',
    classifiers=[
        'Environment :: Web Environment',
        'Framework :: Django',
        'Framework :: Django :: 2.14',  # replace "X.Y" as appropriate
        'Intended Audience :: Developers',
        'License :: OSI Approved :: ${license}',  
        'Operating System :: OS Independent',
        'Programming Language :: Python',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
        'Topic :: Internet :: WWW/HTTP',
        'Topic :: Internet :: WWW/HTTP :: Dynamic Content',
    ],
)