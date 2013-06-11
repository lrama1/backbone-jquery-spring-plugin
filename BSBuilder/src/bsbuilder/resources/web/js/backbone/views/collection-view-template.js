define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',  
  'collections/${className}Collection',
  'text!templates/${className}ListTemplate.htm'
], function($, _, Backbone, Backgrid, ${className}Collection, collectionTemplate){
	
	var ${className}CollectionView = Backbone.View.extend({
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render();	        
	    },
	    render: function(){
	    	console.log("Models" + JSON.stringify(this.collection.models));	    	
	    	var columns = [
		    	#foreach($key in $attrs.keySet() )
		    		#if($foreach.count == 1)
		    			{
		    			name : "",
		    			cell : "select-row",
		    			headerCell: "select-all"
		    			},
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
	          //,footer: Backgrid.Extension.Paginator
	        });
	        //lets expose the grid
	        this.grid = grid;

	       // Initialize the paginator
			var paginator = new Backgrid.Extension.Paginator({
			  collection: this.collection
			});

	        // Render the grid and attach the root to your HTML document
	        #[[
	        this.$el.html(_.template(collectionTemplate, ''));
	        $("#listContainer").html(grid.render().$el);
	        $("#listContainer").append(paginator.render().$el);
	        ]]#
	        
	        this.collection.fetch({reset : true});
	    },
		events : {
			"click #editButton" : function(){
				//alert(this.grid.getSelectedModels());
				//Note: the html for this sample modal is in the ListTemplate.htm 
				//$('#sampleModal').modal({});
				location.hash = "${className.toLowerCase()}/" + this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
			}
		}
	    
	});

  return ${className}CollectionView;
});