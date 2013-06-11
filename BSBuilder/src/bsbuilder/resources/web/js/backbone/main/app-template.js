define([           
  'jquery', 
  'underscore',
  'backbone',
  'router'], function($,_,Backbone, Router){
  var initialize = function(){
    // Pass in our Router module and call it's initialize function
    Router.initialize();
    
    //add handlers for navBar
    $(".navItem").click(function (){
    	$("#navItems").children("li").each(function(){
    		$(this).attr("class", "inactive");
    	});
    	$(this).attr("class", "active");
    });
  };

  return { 
    initialize: initialize
  };
});