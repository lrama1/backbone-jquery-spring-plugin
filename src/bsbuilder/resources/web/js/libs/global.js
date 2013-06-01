define([
  'jquery'
], function($) {
  
  var Global = ({
	  log : function(stringToLog){
		  if(console !== undefined)
			  console.log(stringToLog);
	  }	 
  });

  return Global;

});