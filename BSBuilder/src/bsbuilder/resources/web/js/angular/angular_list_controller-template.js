(
	function(){
		angular.module('${projectName}').controller('${domainClassName}ListController', function(#[[$scope, $http]]#){
			#[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}s',{params :{page : 1, per_page : 1000}}).then(function(value) {
				console.log(value);
				#[[$scope]]#.${domainClassName}s = value.data.rows;
			}, function(reason) {
				
			}, function(value) {
				
			})
		});
	}	
)();
