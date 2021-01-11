module.exports = {
   DB: 'mongodb://${aib.getParam('mongodb.server address')}/${aib.getParam('mongodb.database name')}',
   mongooseUrl: '${aib.getParam("mongodb.mongooseHost")}:${aib.getParam("mongodb.mongoosePort")}'
};