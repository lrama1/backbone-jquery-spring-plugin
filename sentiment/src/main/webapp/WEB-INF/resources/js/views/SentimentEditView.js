define([ 'jquery', 'underscore', 'backbone',
		'text!templates/SentimentEditTemplate.htm', /* the request for this template actually goes thru the MainController*/
		'bootstrap-datepicker' ], function($, _, Backbone, editTemplate,
		datePicker) {

	var SentimentEditView = Backbone.View.extend({
		defaults : {
			model : {}
		},
		//standard backbone function called when a view is constructed
		initialize : function() {
			var view = this;

			$.when().done(function() {
				var data = view.model.toJSON();

				view.render(view.$el, data);
			});

			this.model.on("invalid", function(model, error) {
				alert("Error: " + error);
			});
		},
		render : function(myEl, data) {
			//			$.get("resources/templates/EditTemplate.htm", function(templateText){			
			//				 myEl.html( _.template(templateText, modelToRender.toJSON()) );
			//			});
			myEl.html(_.template(editTemplate, data));
		},
		events : {
			"change input, textarea" : "change", //binding change of any input field to the change function below
			"click #saveSentiment" : "saveSentiment", //binding the saveButton of template using id attr as selector  
			"click #saveNewSentiment" : "saveNewSentiment" //binding the saveNewButton of template using id attr as selector
		},
		saveSentiment : function() {
			this.model.save(null, {
				success : function(model, response, options) {
					$("#operationMessage").html(
							model.get("sentimentText") + " was saved.");
				},
				error : function() {
					alert("error");
				}
			});
		},
		saveNewSentiment : function() {
			this.model.set("id", null);
			this.model.save(null, {
				success : function(model, response, options) {
					$("#operationMessage").html(
							model.get("sentimentText") + " was saved.");
				},
				error : function() {
					alert("error");
				}
			});
		},
		change : function(evt) { //we are relying that the id of the template elements are the same as the model attrs
			var target = evt.target;
			var payLoad = {};
			payLoad[target.id] = target.value;
			//this.model.set(payLoad, {silent : false});
			this.model.set(payLoad, {});
		}

	});

	return SentimentEditView;
});