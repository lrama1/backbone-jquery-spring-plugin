
define([
  'jquery',      
  'underscore',
  'backbone',
  'backgrid',
  'models/${className}Model'
], function($, _, Backbone, backgrid ,${className}Model) {
	var ${className}Collection = Backbone.PageableCollection.extend({
        model: ${className}Model,
        url : '/${projectName}/${className.toLowerCase()}s',
        state: {
            pageSize: 10
        },
        parse : function(response){
  	    	this.state.totalRecords = response.totalRecords;
  	    	//this.state.totalPages = "2";
  	    	this.state.lastPage = response.lastPage;
  	    	console.log("Last Page: " + this.state.lastPage);
  	    	//return response.${className.substring(0,1).toLowerCase()}${className.substring(1)}s;
  	    	return response.rows;
  	    },
  	    
        mode: "server"        
    });
	return ${className}Collection;
});