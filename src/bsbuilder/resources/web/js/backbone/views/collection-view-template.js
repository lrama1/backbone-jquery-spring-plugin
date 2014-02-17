define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',  
  'collections/${className}Collection',
  'globals/global',
  'localizedmessages',
  #if($templateType == "JSP")'text!templates/${className}ListTemplate'#else 'text!templates/${className}ListTemplate.htm'#end /* the request for this template actually goes thru the MainController if its JSP*/
], function($, _, Backbone, Backgrid, ${className}Collection, Global, Messages ,collectionTemplate){
	
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
		    			name : "${className.toLowerCase()}Selector",
		    			cell : "select-row",
		    			headerCell: "select-all"
		    			},
			    		{
			    			name : "${key}",
			    			label : Messages["${key.toUpperCase()}"],
			    			cell : "string"
			    		}
					#else
						,{
			    			name : "${key}",
			    			label : Messages["${key.toUpperCase()}"],
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
	        
	        //this.collection.fetch({reset : true});
	    },
	    events : {
	    	"click #edit${className}Button" : "edit${className}"
	    },
	    edit${className} : function(){
	    	if(this.grid.getSelectedModels().length > 0){
				//location.hash = "${className.toLowerCase()}/" + this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				var idToFetch = this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				require(['views/${className}EditView', 'models/${className}Model'], 
		    		function(${className}EditView, ${className}Model){
				        var ${className.toLowerCase()}  = new ${className}Model({${domainClassIdAttributeName} : idToFetch});	
						var result = ${className.toLowerCase()}.fetch({
							success : function(){
								//render the view when ${className} is fetched successfully	
								//Use this if you want to Edit in a Modal Dialog
								Global.showView(new ${className}EditView({ el: $("#modalEditBody"), model : ${className.toLowerCase()} }));
								$('#myModal').modal('show');
							},
							error : function(){
								alert("problem");
							},
							silent : true
						});
		    		});
				
			}else{
				alert("Please select a ${className} to edit.");
			}
	    }	    
	});

  return ${className}CollectionView;
});