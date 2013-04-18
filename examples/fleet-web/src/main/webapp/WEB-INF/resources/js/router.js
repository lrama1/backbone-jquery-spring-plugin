// Filename: router.js
define([        
'jquery', 
'underscore',
'backbone',
'backgrid',
 'views/VehicleEditView',
 'models/VehicleModel', 
 'collections/VehicleCollection',
 'views/VehicleCollectionView',
 'globals/global'
], function($,_,Backbone, Backgrid, VehicleEditView, VehicleModel, VehicleCollection, VehicleCollectionView, Global) {
	
	//change underscore delims to using {{}}
	_.templateSettings = {
		evaluate :  /{{([\s\S]+?)}}/g,
		interpolate : /{{=([\s\S]+?)}}/g
	};	
  
	var AppRouter = Backbone.Router.extend({
		  routes: {
	          "vehicle/:id": "getVehicle ",    // matches http://example.com/#/vehicle/{id}
	          "vehicles" : "getVehicleList"
	      }
	});
  
	var initialize = function(){
		
		
     	// Initiate the router
	    var app_router = new AppRouter;
		var vehicleView = {};
	    
	    //handler for getting a Vehicle record
	    app_router.on('route:getVehicle', function (idToFetch) {
	        // Note the variable in the route definition being passed in here  
	        var vehicle  = new VehicleModel({vin : idToFetch});			
			var result = vehicle.fetch({
				success : function(){
					//render the view when Vehicle is fetched successfully	
					vehicleView = new VehicleEditView({ el: $("#editContainer"), model : vehicle });
				},
				error : function(){
					alert("problem");
				}
			});
	    });
	    
	    app_router.on('route:getVehicleList', function () {
	    	var vehicleCollectionView = {};
	    	var wrapper = new VehicleCollection();
	    	//alert('getting a list of vehicles' + JSON.stringify(vehicles));
	    	Global.log("getting the list of vehicles.");
//	    	wrapper.fetch({
//	    		success : function(data){
//	    			//alert("List from Server" + JSON.stringify(data));
//	    			//alert("Wrapped Listej " + JSON.stringify(data.toJSON()[0].vehicles));
//	    			vehicleCollectionView = new VehicleCollectionView({ el: $("#editContainer"), collection : new VehicleCollection(data.toJSON()[0].vehicles) });
//	    		},
//	    		error : function(){
//	    			alert("Problem retrieving list");
//	    		}
//	    	});

//	    	$.when(wrapper.fetch()).done(function(data) {
//	    		  alert("Data from callback" + JSON.stringify(data.vehicles));
//	    		  vehicleCollectionView = new VehicleCollectionView({ el: $("#editContainer"), collection : new VehicleCollection(data.vehicles) });
//    		});
	    	vehicleCollectionView = new VehicleCollectionView({ el: $("#editContainer"), collection : new VehicleCollection()});
	    });
	    
	    // Start Backbone history a necessary step for bookmarkable URL's
	    Backbone.history.start();

	};
	
  return { 
    initialize: initialize
  };
});