define([ 
  'jquery',
  'underscore',
  'backbone',
  'text!templates/${className}EditTemplate.htm'
], function($, _, Backbone, editTemplate){
	
	var ${className}EditView = Backbone.View.extend({
		defaults : {model : {}},
	    initialize: function(){    	
	        this.render(this.$el, this.model);
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
	    	//alert(JSON.stringify(this.model));
	    	this.model.save();
	    },
	    saveNew${className} : function(){
	    	this.model.set("${domainClassIdAttributeName}", null);
	    	this.model.save();
	    },
	    change : function(){  //we are relying that the id of the template elements are the same as the model attrs
	    	var target = event.target;        
	        this.model.set(target.id, target.value);
	    }
	    
	});

  return ${className}EditView;
});