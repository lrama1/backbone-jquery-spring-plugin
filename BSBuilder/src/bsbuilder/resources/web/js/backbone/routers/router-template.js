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
		showView: function (view) {
	        //destroy current view -- we need this to address the 'ghost' view problem
			//of inherent to apps using Backbone
			if(this.currentView !== undefined){
				console.log("calling cleanup");
				this.currentView.unbind();
				this.currentView.undelegateEvents();
			}
			//create new view
			this.currentView = view;
			this.currentView.delegateEvents();	 
			return this.currentView;
		},
		routes: {
		#parse("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-02.js")
		}		
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