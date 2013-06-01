define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',
  'backgrid-paginator',
  'collections/${className}Collection',
  'text!templates/${className}ListTemplate.htm'
], function($, _, Backbone, Backgrid, BackgridPaginator, ${className}Collection, collectionTemplate){
	
	var ${className}CollectionView = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render(this.collection);	        
	    },
	    render: function(){
	    	console.log("Models" + JSON.stringify(this.collection.models));	    	
	    	var columns = [
		    	#foreach($key in $attrs.keySet() )
		    		#if($foreach.count == 1)
			    		{
			    			name : "${key}",
			    			label : "${key.toUpperCase()}",
			    			cell : "string"
			    		}
					#else
						,{
			    			name : "${key}",
			    			label : "${key.toUpperCase()}",
			    			cell : "string"
			    		}
					#end
		    	#end
	    	];

	        // Initialize a new Grid instance
	        var grid = new Backgrid.Grid({
	          columns: columns,
	          collection: this.collection,
	          footer: Backgrid.Extension.Paginator
	        });

	        // Render the grid and attach the root to your HTML document	        	
	        $("#editContainer").html(grid.render().$el);
	        this.collection.fetch({reset : true});
	    }
	    
	});

  return ${className}CollectionView;
});