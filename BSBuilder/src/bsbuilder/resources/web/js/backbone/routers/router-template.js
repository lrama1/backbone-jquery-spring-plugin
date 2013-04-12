// Filename: router.js
define([        
'jquery', 
'underscore',
'backbone',
 'views/${className}EditView',
 'models/${className}Model'
], function($,_,Backbone, ${className}EditView, ${className}Model) {
	
	//change underscore delims to using {{}}
	_.templateSettings = {
		evaluate :  /{{([\s\S]+?)}}/g,
		interpolate : /{{=([\s\S]+?)}}/g
	};	
  
	var AppRouter = Backbone.Router.extend({
		  routes: {
	          "${className.toLowerCase()}/:id": "get${className} " // matches http://example.com/#/${className.toLowerCase()}/{id}
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
	    
	    // Start Backbone history a necessary step for bookmarkable URL's
	    Backbone.history.start();

	};
	
  return { 
    initialize: initialize
  };
});