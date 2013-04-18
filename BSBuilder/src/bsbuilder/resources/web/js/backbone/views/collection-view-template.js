define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',
  //'text!templates/ListTemplate.htm'
], function($, _, Backbone, Backgrid){
	
	var ${className}CollectionView = Backbone.View.extend({
	    initialize: function(){    	
	        this.render(this.$el, this.collection);	        
	    },
	    render: function(myEl, modelToRender){    	
	    	
	    	console.log("MOdels" + JSON.stringify(this.collection.models));
	    	var data = {
	    			${className.toLowerCase()}s: this.collection.models,
	    	        _: _ 
	    	      };
	    	
	    	//myEl.html( _.template(collectionTemplate, data) );
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
	        	  collection: this.collection
	        	});

	        	// Render the grid and attach the root to your HTML document
	        	//$("#editContainer").append(grid.render().$el);
	        	$("#editContainer").html(grid.render().$el);
	    }
	    
	});

  return ${className}CollectionView;
});