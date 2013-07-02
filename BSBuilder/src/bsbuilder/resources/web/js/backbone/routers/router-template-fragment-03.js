		//FROM a Fragment
		//handler for getting a ${className} record
	    app_router.on('route:get${className}', function (idToFetch) {
	        // Note the variable in the route definition being passed in here
	    	require(['views/${className}EditView', 'models/${className}Model'], 
	    		function(${className}EditView, ${className}Model){
			        var ${className.toLowerCase()}  = new ${className}Model({${domainClassIdAttributeName} : idToFetch});	
					var result = ${className.toLowerCase()}.fetch({
						success : function(){
							//render the view when ${className} is fetched successfully	
							app_router.showView(new ${className}EditView({ el: $("#bodyContainer"), model : ${className.toLowerCase()} }));
						},
						error : function(){
							alert("problem");
						},
						silent : true
					});
	    		});
	    });
	    
	    app_router.on('route:get${className}List', function () {
	    	Global.log("getting the list of ${className}s.");
	    	require(['collections/${className}Collection','views/${className}CollectionView'], 
	    	    function(${className}Collection, ${className}CollectionView){
	    	        app_router.showView(new ${className}CollectionView({ el: $("#bodyContainer"), collection : new ${className}Collection()}));
	    	    });
	    });
	    