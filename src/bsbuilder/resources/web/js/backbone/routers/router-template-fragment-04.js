		
	    #if($compositePresenterName)
    	app_router.on('route:show${compositePresenterName}', function() {
				console.log("showing ${compositePresenterName}.");
				require([ 		'presenters/${compositePresenterName}' ], function(
						${compositePresenterName}) {
					Global.showView(new ${compositePresenterName}({
						el : $("#bodyContainer")
					}));
	
				});
		});	
	    #end	
	    