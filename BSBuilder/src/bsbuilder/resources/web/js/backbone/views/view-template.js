define([ 
  'jquery',
  'underscore',
  'backbone',
  'text!templates/${className}EditTemplate.htm',
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
	    	this.model.save({
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
                        ${key} : Date.parse(this.model.get("${key}"))
	    			#end	
	    		#end
	    	});	    	
	    },
	    saveNew${className} : function(){
	    	this.model.set("${domainClassIdAttributeName}", null);
	    	this.model.save({
	    		#foreach($key in $attrs.keySet() )
	    			#if ($attrs.get($key) == "java.util.Date")
                        ${key} : Date.parse(this.model.get("${key}"))
	    			#end	
	    		#end
	    	});
	    },
	    change : function(evt){  //we are relying that the id of the template elements are the same as the model attrs
	    	var target = evt.target;        	        
	        var payLoad = {};
			payLoad[target.id] = target.value;
			this.model.set(payLoad, {silent : false});
	    }
	    
	});

  return ${className}EditView;
});