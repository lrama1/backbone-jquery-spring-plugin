define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',
  'backgrid-paginator',
  'collections/VehicleCollection',
  'text!templates/ListTemplate.htm'
], function($, _, Backbone, Backgrid, BackgridPaginator, VehicleCollection, collectionTemplate){
	
	var VehicleCollectionView = Backbone.View.extend({
	    initialize: function(){    	
	        this.render(this.$el, this.collection);
      
	    },	    
	    render: function(myEl, modelToRender){    	
	    	var columns = [{
	        	  name: "vin", // The key of the model attribute
	        	  label: "VIN", // The name to display in the header
	        	  editable: false, // By default every cell in a column is editable, but *ID* shouldn't be
	        	  // Defines a cell type, and ID is displayed as an integer without the ',' separating 1000s.
	        	  cell: "string"
	        	}, {
	        	  name: "model",
	        	  label: "Model",
	        	  // The cell type can be a reference of a Backgrid.Cell subclass, any Backgrid.Cell subclass instances like *id* above, or a string
	        	  cell: "string" // This is converted to "StringCell" and a corresponding class in the Backgrid package namespace is looked up
	        	}
	        	];

	    		
	    		var grid = new Backgrid.Grid({
	        	  columns: columns,
	        	  collection: this.collection,
	        	  footer: Backgrid.Extension.Paginator
	        	});
	    		$("#editContainer").append(grid.render().$el);

	    		this.collection.fetch({reset : true});
	        	// Render the grid and attach the root to your HTML document	        	
//	        	$.when(this.collection.fetch()).done(function(data){	        		
//	        		this.collection = new VehicleCollection(data);
//	        		alert("Done fetching : " + JSON.stringify(data));
//	        		// Initialize a new Grid instance
//	        		
//	        	});	        	
	    }	    
	    
	});

  return VehicleCollectionView;
});