
define([
  'jquery',      
  'underscore',
  'backbone',
  'models/${className}Model'
], function($, _, Backbone, ${className}Model) {


	var ${className}Collection = Backbone.Collection.extend({
        model: ${className}Model,
        url : '/${projectName}/controller/${className.toLowerCase()}s'
    });

	return ${className}Collection;
});