define([
  'jquery',      
  'underscore',
  'backbone'
], function($, _, Backbone) {
  
  var ${className}Model = Backbone.Model.extend({
	  idAttribute : '${domainClassIdAttributeName}',	
	  urlRoot : '/${projectName}/${className.toLowerCase()}',	  
	  validate : function(attributes){
		//alert('test');
	  }	,
	  parse : function(response){		  
		  #foreach($key in $attrs.keySet() )
		  	#if($attrs.get($key) == 'java.util.Date')
		  		response.$key = new Date(response.$key);
		  	#end	
		  #end
		  return response;
	  }
  });

  return ${className}Model;

});