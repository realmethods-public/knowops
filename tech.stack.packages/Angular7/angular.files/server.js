// server.js
const express = require('express'),
	path = require('path'),
	bodyParser = require('body-parser'), 
	cors = require('cors'),
	mongoose = require('mongoose'),
#foreach( $class in $aib.getClassesToGenerate() )  
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
    ${lowercaseClassName}Routes = require('./src/app/expressRoutes/${className}Routes'),
#end##foreach( $class in $aib.getClassesToGenerate() )
	config = require('./config/mongoDb.js');

mongoose.Promise = global.Promise;
mongoose.connect(process.env.MONGO_HOST_ADDRESS || config.DB, {
    reconnectTries: Number.MAX_VALUE,
    reconnectInterval: 1000
  }).then(
    () => {console.log('Database is connected') },
    err => { console.log('Can not connect to the database: ' + err)}
  );

const app = express();
app.use(bodyParser.json());
app.use(cors());
const port = ${aib.getParam("mongodb.mongoosePort")};

#foreach( $class in $aib.getClassesToGenerate() )  
#set( $className = $class.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
app.use('/${className}', ${lowercaseClassName}Routes);
#end##foreach( $class in $aib.getClassesToGenerate() )

const server = app.listen(port, function(){
  console.log('Listening on port ' + port);
});
