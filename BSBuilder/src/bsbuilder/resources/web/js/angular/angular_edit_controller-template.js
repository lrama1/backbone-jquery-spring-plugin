(
	function(){
		angular.module('${projectName}').controller('${domainClassName}EditController', function(#[[$scope, $routeParams, $http]]#){
			console.log(#[[$routeParams]]#.id);

			#[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}/' + #[[$routeParams]]#.id).then(function(value) {
				console.log(value);
				#[[$scope]]#.${domainClassName} = value.data;
			}, function(reason) {
				
			}, function(value) {
				
			});
			
			#[[$scope]]#.doSave = function(){
				alert('Saving:' + JSON.stringify(#[[$scope]]#.${domainClassName}));				
			}	
			
		});
	}	
)();
