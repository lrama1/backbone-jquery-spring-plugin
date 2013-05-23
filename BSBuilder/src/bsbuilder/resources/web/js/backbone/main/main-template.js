// Require.js allows us to configure shortcut alias (so that we don't always type the full path)
// Their usage will become more apparent futher along in the tutorial.
require.config({
  paths: {
	jquery:     '/${projectName}/resources/js/libs/jquery',
    underscore: '/${projectName}/resources/js/libs/underscore',
    backbone:   '/${projectName}/resources/js/libs/backbone',
    backgrid :         '/${projectName}/resources/js/libs/backgrid',
    'backbone-pageable': '/${projectName}/resources/js/libs/backbone-pageable',
    'backgrid-paginator': '/${projectName}/resources/js/libs/backgrid-paginator',
    'bootstrap-datepicker': '/${projectName}/resources/js/libs/bootstrap-datepicker',
    templates:  '/${projectName}/resources/templates'	
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