

${className} = Backbone.Model.extend({	
	defaults : {firstName : "", lastName : ""},
    initialize: function(){        
        this.bind("error", function(model, error){
            // We have received an error, log it, alert it or forget it :)
            alert("Error:" + JSON.stringify(error));
            return false;
        });
    }
	,	
	urlRoot : '/${projectName}/controller/${className.toLowerCase()}'
	,
	validate : function(attributes){
		
	}	
});


${className}EditView = Backbone.View.extend({
	defaults : {model : {}},
    initialize: function(){    	
        this.render(this.$el, this.model);
    },
    render: function(myEl, modelToRender){    	
		$.get("resources/templates/EditTemplate.htm", function(templateText){			
			 myEl.html( _.template(templateText, modelToRender.toJSON()) );
		});

    },
    events: {
        "change input": "change",   //binding change of any input field to the change function below
        "click #save${className}": "save${className}",  //binding the saveButton of template using id attr as selector  
     	"click #saveNew${className}": "saveNew${className}"  //binding the saveNewButton of template using id attr as selector
    },
    save${className} : function(){
    	alert(JSON.stringify(this.model));
    	this.model.save();
    },
    saveNew${className} : function(){
    	this.model.set("id", null);
    	this.model.save();
    },
    change : function(){
    	var target = event.target;        
        this.model.set(target.id, target.value);
    }
    
});