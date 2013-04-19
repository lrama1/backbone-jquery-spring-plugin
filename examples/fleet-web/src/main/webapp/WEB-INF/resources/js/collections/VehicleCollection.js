
define([
  'jquery',      
  'underscore',
  'backbone',
  'backgrid',
  'models/VehicleModel'
], function($, _, backbone, backgrid, VehicleModel) {
	var VehicleCollection = Backbone.PageableCollection.extend({
        model: VehicleModel,
        url : '/fleet-web/controller/vehicles',
        state: {
            pageSize: 5
        },
        parse : function(response){
  	    	this.state.totalRecords = response.totalRecords;
  	    	//this.state.totalPages = "2";
  	    	this.state.lastPage = response.lastPage;
  	    	console.log("Last Page: " + this.state.lastPage);
  	    	//alert(JSON.stringify(this.state));
  	    	return response.vehicles;
  	    },
  	    
        mode: "server"        
    });
	return VehicleCollection;
});