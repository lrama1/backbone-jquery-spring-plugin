

Vehicle = Backbone.Model.extend({	
	//defaults : {firstName : "", lastName : ""},
	idAttribute : 'vin'
	,	
	urlRoot : '/fleet-web/controller/vehicle'
	,
	validate : function(attributes){
		
	}	
});


VehicleEditView = Backbone.View.extend({
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
        "click #saveVehicle": "saveVehicle",  //binding the saveButton of template using id attr as selector  
     	"click #saveNewVehicle": "saveNewVehicle"  //binding the saveNewButton of template using id attr as selector
    },
    saveVehicle : function(){
    	//alert(JSON.stringify(this.model));
    	this.model.save();
    },
    saveNewVehicle : function(){
    	this.model.set("vin", null);
    	this.model.save();
    },
    change : function(){
    	var target = event.target;        
        this.model.set(target.id, target.value);
    }
    
});