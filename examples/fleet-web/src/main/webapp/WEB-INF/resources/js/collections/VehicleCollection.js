
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
//        state: {
//            pageSize: 5
//            //,
//            //totalRecords : 20
//        }, 
        parse : function(response){
  	    	alert("Parsings " + JSON.stringify(response.vehicles));
  	    	//data.toJSON()[0].vehicles
  	    	//this.reset(response.vehicles);
  	    	this.state.pageSize = 5;
  	    	this.state.totalRecords = response.totalRecords;
  	    	alert(this.state.totalRecords);
  	    	return response.vehicles;
  	    },  
//        fetch : function(){
//        	// store reference for this collection
//        	alert("paramx");
//        	var deferred = $.Deferred();
//            var collection = this;
//            $.ajax({
//                type : 'GET',
//                url : this.url,
//                dataType : 'json',
//                success : function(data) {
//                	console.log("Done FETCHING");
//                    console.log(data);
//                    // set collection data (assuming you have retrieved a json object)
//                    collection.reset(data);
//                    deferred.resolve(data);
//                }
//            });
//            return deferred.promise();
//        } ,
        mode: "server" // page entirely on the client side
        
    });

	return VehicleCollection;
});