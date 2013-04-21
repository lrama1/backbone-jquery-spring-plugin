define([
  'jquery',      
  'underscore',
  'backbone'
], function($, _, Backbone) {
  
  var ${className}Model = Backbone.Model.extend({
	  idAttribute : '${domainClassIdAttributeName}',	
	  urlRoot : '/${projectName}/controller/${className.toLowerCase()}',	  
	  validate : function(attributes){
		//alert('test');
	  }	
  });

  return ${className}Model;

});