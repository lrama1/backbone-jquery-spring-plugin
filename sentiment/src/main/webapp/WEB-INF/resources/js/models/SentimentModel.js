define([ 'jquery', 'underscore', 'backbone' ], function($, _, Backbone) {

	var SentimentModel = Backbone.Model.extend({
		idAttribute : 'id',
		urlRoot : '/sentiment/sentiment',
		validate : function(attributes, options) {
			//sample validation. uncomment if u want to use it
			//if(attributes.<ATTRBITE_NAME_HERE> == ''){				
			//	return '<ATTR_NAME> cant be blank.';
			//}		
		},
		parse : function(response) {
			return response;
		}
	});

	return SentimentModel;

});