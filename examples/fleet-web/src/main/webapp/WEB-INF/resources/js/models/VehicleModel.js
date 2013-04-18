define([
  'jquery',      
  'underscore',
  'backbone'
], function($, _, Backbone) {
  
  var VehicleModel = Backbone.Model.extend({
	  idAttribute : 'vin',	
	  urlRoot : '/fleet-web/controller/vehicle'
			,
			validate : function(attributes){
				alert('test');
			}	
  });

  return VehicleModel;

});