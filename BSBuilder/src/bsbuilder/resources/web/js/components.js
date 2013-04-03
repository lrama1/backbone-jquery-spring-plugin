

Person = Backbone.Model.extend({	
	defaults : {firstName : "", lastName : ""},
    initialize: function(){        
        this.bind("error", function(model, error){
            // We have received an error, log it, alert it or forget it :)
            alert("Error:" + JSON.stringify(error));
            return false;
        });
    }
	,	
	urlRoot : '/XBackbone/controller/person'
	,
	validate : function(attributes){
		
	}	
});


PersonEditView = Backbone.View.extend({
	defaults : {model : {}},
    initialize: function(){    	
        this.render(this.$el, this.model);
    },
    render: function(myEl, modelToRender){    	
//		$.get("resources/templates/EditTemplate.htm", function(templateText){			
//			 myEl.html( _.template(templateText, modelToRender.toJSON()) );
//		});
    	myEl.html( _.template($("#personTemplatex").html(), modelToRender.toJSON()) );
    },
    events: {
        "change input": "change",   //binding change of any input field to the change function below
        "click #savePerson": "savePerson",  //binding the saveButton of template using id attr as selector  
     	"click #saveNewPerson": "saveNewPerson"  //binding the saveNewButton of template using id attr as selector
    },
    savePerson : function(){
    	alert(JSON.stringify(this.model));
    	this.model.save();
    },
    saveNewPerson : function(){
    	this.model.set("id", null);
    	this.model.save();
    },
    change : function(){
    	var target = event.target;
        //alert('changing ' + target.id +  ' with name:' + target.name +' from: ' + target.defaultValue + ' to: ' + target.value);
        this.model.set(target.id, target.value);
    }
    
});