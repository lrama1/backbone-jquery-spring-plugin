		//FROM a Fragment
		//handler for getting a ${className} record
	    app_router.on('route:get${className}', function (idToFetch) {
	        // Note the variable in the route definition being passed in here  
	        var ${className.toLowerCase()}  = new ${className}Model({${domainClassIdAttributeName} : idToFetch});	
			var result = ${className.toLowerCase()}.fetch({
				success : function(){
					//render the view when ${className} is fetched successfully	
					var ${className.toLowerCase()}View = new ${className}EditView({ el: $("#editContainer"), model : ${className.toLowerCase()} });
				},
				error : function(){
					alert("problem");
				}
			});
	    });
	    
	    app_router.on('route:get${className}List', function () {
	    	Global.log("getting the list of ${className}s.");
	    	var ${className.toLowerCase()}CollectionView = new ${className}CollectionView({ el: $("#editContainer"), collection : new ${className}Collection()});
	    });
	    