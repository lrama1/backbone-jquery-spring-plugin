
define([
  'jquery',      
  'underscore',
  'backbone',
  'backgrid',
  'models/${className}Model'
], function($, _, Backbone, backgrid ,${className}Model) {
	var ${className}Collection = Backbone.PageableCollection.extend({
        model: ${className}Model,
        url : '/${projectName}/controller/${className.toLowerCase()}s',
        state: {
            pageSize: 5
        },
        parse : function(response){
  	    	this.state.totalRecords = response.totalRecords;
  	    	//this.state.totalPages = "2";
  	    	this.state.lastPage = response.lastPage;
  	    	console.log("Last Page: " + this.state.lastPage);
  	    	//alert(JSON.stringify(this.state));
  	    	return response.${className.toLowerCase()}s;
  	    },
  	    
        mode: "server"        
    });
	return ${className}Collection;
});