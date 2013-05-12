		//FROM a Fragment
		//handler for getting a ${className} record
	    app_router.on('route:get${className}', function (idToFetch) {
	        // Note the variable in the route definition being passed in here  
	        var ${className.toLowerCase()}  = new ${className}Model({${domainClassIdAttributeName} : idToFetch});	
	        var ${className.toLowerCase()}View = {};
			var result = ${className.toLowerCase()}.fetch({
				success : function(){
					//render the view when ${className} is fetched successfully	
					${className.toLowerCase()}View = new ${className}EditView({ el: $("#editContainer"), model : ${className.toLowerCase()} });
				},
				error : function(){
					alert("problem");
				}
			});
	    });
	    
	    app_router.on('route:get${className}List', function () {
	    	var ${className.toLowerCase()}CollectionView = {};
	    	var ${className.toLowerCase()}s = new ${className}Collection();
	    	Global.log("getting the list of ${className}s.");
	    	${className.toLowerCase()}CollectionView = new ${className}CollectionView({ el: $("#editContainer"), collection : new ${className}Collection()});
	    });
	    