// Filename: router.js
define([        
 'jquery', 
 'underscore',
 'backbone',
 'backgrid',  
 'backgrid-paginator',
 'backgrid-select-all',
 'bootstrap.min',
 'globals/global'
  
], function($,_,Backbone, Backgrid, BackgridPaginator, BackgridSelectAll ,Bootstrap, Global) {
	
	//change underscore delims to using {{}}
	_.templateSettings = {
		evaluate :  /{{([\s\S]+?)}}/g,
		interpolate : /{{=([\s\S]+?)}}/g
	};	
  
	var AppRouter = Backbone.Router.extend({		
		routes: {
		#parse("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-02.js")
		}		
	});
	
	$(document).ready(function () {
	    $('.nav li a').click(function(e) {
	        $('.nav li').removeClass('active');
	        var $parent = $(this).parent();
	        if (!$parent.hasClass('active')) {
	            $parent.addClass('active');
	        }
	        //e.preventDefault();
	    });
	});
	
	var initialize = function(){		
		
     	// Initiate the router
	    var app_router = new AppRouter;		
	    
	    #parse("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-03.js")
	    
	    // Start Backbone history a necessary step for bookmarkable URL's
	    Backbone.history.start();

	};
	
  return { 
    initialize: initialize
  };
});