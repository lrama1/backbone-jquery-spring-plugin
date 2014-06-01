define([ 
  'jquery',
  'underscore',
  'backbone',
  'backgrid',  
  'collections/${domainClassName}Collection',
  'globals/global',
  #if($injectMessages)'localizedmessages',#end
  #if($templateType == "JSP")'text!templates/${domainClassName}ListTemplate'#else 'text!templates/${domainClassName}ListTemplate.htm'#end /* the request for this template actually goes thru the MainController if its JSP*/
], function($, _, Backbone, Backgrid, ${domainClassName}Collection, Global, #if($injectMessages)Messages ,#end collectionTemplate){
	
	var ${domainClassName}CollectionView = Backbone.View.extend({
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
		    			name : "${domainClassName.toLowerCase()}Selector",
		    			cell : "select-row",
		    			headerCell: "select-all"
		    			},
			    		{
			    			name : "${key}",
			    			#if($injectMessages)label : Messages["${key.toUpperCase()}"],#end
			    			cell : "string"
			    		}
					#else
						,{
			    			name : "${key}",
			    			#if($injectMessages)label : Messages["${key.toUpperCase()}"],#end
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
			#set( $attrVar = "" )
			#foreach($key in $attrs.keySet() )
				#if($foreach.count == 1)
					#set( $attrVar = $key + ':' + "''" )
				#else
					#set( $attrVar = $attrVar + ', ' + $key + ':' + "''" )
				#end	
			#end
			
	        #[[
	        this.$el.html(_.template(collectionTemplate, ]]#  {$attrVar}  #[[));   
	        $("#listContainer").html(grid.render().$el);
	        $("#listContainer").append(paginator.render().$el);
	        ]]#
	        
	        //this.collection.fetch({reset : true});
	    },
	    events : {
	    	"click #edit${domainClassName}Button" : "edit${domainClassName}",
	    	"click #filterButton" : "filter"
	    },
	    edit${domainClassName} : function(){
	    	if(this.grid.getSelectedModels().length > 0){
				//location.hash = "${domainClassName.toLowerCase()}/" + this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				var idToFetch = this.grid.getSelectedModels()[0].get("${domainClassIdAttributeName}");
				require(['views/${domainClassName}EditView', 'models/${domainClassName}Model'], 
		    		function(${domainClassName}EditView, ${domainClassName}Model){
				        var ${domainClassName.toLowerCase()}  = new ${domainClassName}Model({${domainClassIdAttributeName} : idToFetch});	
						var result = ${domainClassName.toLowerCase()}.fetch({
							success : function(){
								//render the view when ${domainClassName} is fetched successfully	
								//Use this if you want to Edit in a Modal Dialog
								Global.showView(new ${domainClassName}EditView({ el: $("#modalEditBody"), model : ${domainClassName.toLowerCase()} }));
								$('#myModal').modal('show');
							},
							error : function(){
								alert("problem");
							},
							silent : true
						});
		    		});
				
			}else{
				alert("Please select a ${domainClassName} to edit.");
			}
	    },
		filter : function(){
			#set( $attrSelectors = "" )
			#foreach($key2 in $attrs.keySet() )
				#if($foreach.count == 1)
					#set( $attrSelectors = "'" + $key2 + "' + ':' + " + 'this.$("#' + ${key2} + '").val()' )
				#else
					#set( $attrSelectors = $attrSelectors + " + ';' + '" + $key2 + "' + ':' + " + 'this.$("#' + ${key2} + '").val()' )
				#end	
			#end
			this.collection.queryParams.filter=$attrSelectors;
			alert('filtering the list');
			this.collection.fetch();
		}	    
	});

  return ${domainClassName}CollectionView;
});