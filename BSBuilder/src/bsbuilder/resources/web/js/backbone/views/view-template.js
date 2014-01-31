define([ 
  'jquery',
  'underscore',
  'backbone',
  #if($templateType == "JSP")'text!templates/${className}EditTemplate'#else 'text!templates/${className}EditTemplate.htm'#end, /* the request for this template actually goes thru the MainController*/
  'bootstrap-datepicker'
], function($, _, Backbone, editTemplate, datePicker){
	
	var ${className}EditView = Backbone.View.extend({
		defaults : {model : {}},
		//standard backbone function called when a view is constructed
	    initialize: function(){    	
	        this.render(this.$el, this.model);
	        #foreach($key in $attrs.keySet() )
	        	#if ($attrs.get($key) == "java.util.Date")
	        		$('#${key}').datepicker({
	    	    		format: 'mm-dd-yyyy',
	    	    		setTime : 'true'
	    	    	});
	        	#end	
	        #end
	        this.model.on("invalid", function(model, error) {
	        	  alert("Error: " + error);
	        });
	    },
	    render: function(myEl, modelToRender){    	
//			$.get("resources/templates/EditTemplate.htm", function(templateText){			
//				 myEl.html( _.template(templateText, modelToRender.toJSON()) );
//			});
	    	myEl.html( _.template(editTemplate, modelToRender.toJSON()) );

	    },
	    events: {
	        "change input": "change",   //binding change of any input field to the change function below
	        "click #save${className}": "save${className}",  //binding the saveButton of template using id attr as selector  
	     	"click #saveNew${className}": "saveNew${className}"  //binding the saveNewButton of template using id attr as selector
	    },
	    save${className} : function(){
	    	this.model.save(null,{
	    		success : function(model, response, options){
	    			#set( $attributeNameIterator =  $attrs.keySet().iterator())	    			
	    			#set( $firstAttr = $attributeNameIterator.next().toString())
	    			#set( $secondAttr = $attributeNameIterator.next().toString())
					$("#operationMessage").html(model.get("${secondAttr}") + " was saved.");
				},
				error : function(){
					alert("error");
				}
	    		#set( $dateFieldCount = 0 )
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
	    				,	
                        ${key} : Date.parse(this.model.get("${key}"))
                        #set( $dateFieldCount = $dateFieldCount + 1 )
	    			#end	
	    		#end
	    	});
	    },
	    saveNew${className} : function(){
	    	this.model.set("${domainClassIdAttributeName}", null);
	    	this.model.save(null,{
	    		success : function(model, response, options){
	    			#set( $attributeNameIterator =  $attrs.keySet().iterator())	    			
	    			#set( $firstAttr = $attributeNameIterator.next().toString())
	    			#set( $secondAttr = $attributeNameIterator.next().toString())
					$("#operationMessage").html(model.get("${secondAttr}") + " was saved.");
				},
				error : function(){
					alert("error");
				}
	    		#set( $dateFieldCount = 0 )
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
	    				,	
                        ${key} : Date.parse(this.model.get("${key}"))
                        #set( $dateFieldCount = $dateFieldCount + 1 )
	    			#end	
	    		#end
	    	});
	    },
	    change : function(evt){  //we are relying that the id of the template elements are the same as the model attrs
	    	var target = evt.target;        	        
	        var payLoad = {};
			payLoad[target.id] = target.value;
			//this.model.set(payLoad, {silent : false});
			this.model.set(payLoad, {});
	    }
	    
	});

  return ${className}EditView;
});