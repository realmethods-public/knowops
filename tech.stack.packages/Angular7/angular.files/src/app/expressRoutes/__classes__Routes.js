#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
// ${lowercaseClassName}Routes.js

var express = require('express');
var app = express();
var ${lowercaseClassName}Routes = express.Router();

// Require Item model in our routes module
var $className = require('../models/${className}');

// Defined store route
${lowercaseClassName}Routes.route('/add').post(function (req, res) {
	var ${lowercaseClassName} = new ${className}(req.body);
	${lowercaseClassName}.save()
    .then(item => {
    	res.status(200).json({'${lowercaseClassName}': '${className} added successfully'});
    })
    .catch(err => {
    	res.status(400).send("unable to save to database");
    });
});

// Defined get data(index or listing) route
${lowercaseClassName}Routes.route('/').get(function (req, res) {
	${className}.find(function (err, ${lowercaseClassName}s){
		if(err){
			console.log(err);
		}
		else {
			res.json(${lowercaseClassName}s);
		}
	});
});

// Defined edit route
${lowercaseClassName}Routes.route('/edit/:id').get(function (req, res) {
	var id = req.params.id;
	${className}.findById(id, function (err, ${lowercaseClassName}){
		res.json(${lowercaseClassName});
	});
});

//  Defined update route
${lowercaseClassName}Routes.route('/update/:id').post(function (req, res) {
	${className}.findById(req.params.id, function(err, ${lowercaseClassName}) {
		if (!${lowercaseClassName})
			return next(new Error('Could not load a ${className} Document using id ' + req.params.id));
		else {
#set( $includePrimaryKeys = false )
#set( $includePrimaryKeys = false )
#set( $attributes = ${classObject.getAttributesOrderedInHierarchy( $includePrimaryKeys )} )
#foreach( $attribute in $attributes )      
            ${lowercaseClassName}.${attribute.getName()} = req.body.${attribute.getName()};
#end##foreach ( $attribute in $classObject.getAttributesOrdered( $includePrimaryKeys ) )

			${lowercaseClassName}.save().then(${lowercaseClassName} => {
				res.json('Update complete');
			})
			.catch(err => {
				res.status(400).send("unable to update the database");
			});
		}
	});
});

// Defined delete | remove | destroy route
${lowercaseClassName}Routes.route('/delete/:id').get(function (req, res) {
   ${className}.findOneAndDelete({_id: req.params.id}, function(err, ${lowercaseClassName}){
        if(err) res.json(err);
        else res.json('Successfully removed ' + $className + ' using id ' + req.params.id );
    });
});

module.exports = ${lowercaseClassName}Routes;