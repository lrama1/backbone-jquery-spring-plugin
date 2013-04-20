// Filename: router.js
define([        
 'jquery', 
 'underscore',
 'backbone',
 'backgrid',
  'views/${className}EditView',
  'models/${className}Model', 
  'collections/${className}Collection',
  'views/${className}CollectionView',
  'globals/global'
  
], function($,_,Backbone, Backgrid ,${className}EditView, ${className}Model, ${className}Collection, ${className}CollectionView, Global) {
	
	//change underscore delims to using {{}}
	_.templateSettings = {
		evaluate :  /{{([\s\S]+?)}}/g,
		interpolate : /{{=([\s\S]+?)}}/g
	};	
  
	var AppRouter = Backbone.Router.extend({
		  routes: {
	          "${className.toLowerCase()}/:id": "get${className} ", // matches http://example.com/#/${className.toLowerCase()}/{id}
	          "${className.toLowerCase()}s" : "get${className}List" 
	      }
	});
  
	var initialize = function(){
		
		
     	// Initiate the router
	    var app_router = new AppRouter;
		var ${className.toLowerCase()}View = {};
	    
	    //handler for getting a ${className} record
	    app_router.on('route:get${className}', function (idToFetch) {
	        // Note the variable in the route definition being passed in here  
	        var ${className.toLowerCase()}  = new ${className}Model({${domainClassIdAttributeName} : idToFetch});			
			var result = ${className.toLowerCase()}.fetch({
				success : function(){
					//render the view when ${className} is fetched successfully	
					${className.toLowerCase()}View = new ${className}EditView({ el: $("#editContainer"), model : ${className.toLowerCase()} });
				},
				error : function(){
					alert("problem");
				}
			});
	    });
	    
	    app_router.on('route:get${className}List', function () {
	    	var ${className.toLowerCase()}CollectionView = {};
	    	var ${className.toLowerCase()}s = new ${className}Collection();
	    	Global.log("getting the list of ${className}s.");
	    	${className.toLowerCase()}CollectionView = new ${className}CollectionView({ el: $("#editContainer"), collection : new ${className}Collection()});
	    });
	    
	    // Start Backbone history a necessary step for bookmarkable URL's
	    Backbone.history.start();

	};
	
  return { 
    initialize: initialize
  };
});