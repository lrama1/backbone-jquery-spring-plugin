// Require.js allows us to configure shortcut alias (so that we don't always type the full path)
// Their usage will become more apparent futher along in the tutorial.
require.config({
  paths: {
    jquery:     '/fleet-web/resources/js/libs/jquery',
    underscore: '/fleet-web/resources/js/libs/underscore',
    backbone:   '/fleet-web/resources/js/libs/backbone',
    backgrid :         '/fleet-web/resources/js/libs/backgrid',
    'backbone-pageable': '/fleet-web/resources/js/libs/backbone-pageable',
    'backgrid-paginator': '/fleet-web/resources/js/libs/backgrid-paginator',
    templates:  '/fleet-web/resources/templates'
    	
  },
  shim: {
	    underscore: {
	      exports: '_'
	    },
	    backbone: {
	      deps: ["underscore", "jquery"],
	      exports: "Backbone"
	    },	    
        backgrid: {
            exports: "Backgrid",
            deps: ['underscore', 'backbone', 'backbone-pageable'],
            init: function(_, Backbone, PageableCollection) {
                this.Backbone.PageableCollection = PageableCollection;
            }
        }	    ,
        'backgrid-paginator' : {
	    	deps: ['underscore', 'backbone', 'backgrid']
	    }
        
  }

});

require([
  // Load our app module and pass it to our definition function
  'app',

], function(App){
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
  App.initialize();
});