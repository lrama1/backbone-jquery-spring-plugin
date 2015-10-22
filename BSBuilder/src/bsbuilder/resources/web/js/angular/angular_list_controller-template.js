#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
		angular.module('${projectName}').controller('${domainClassName}ListController', function(#[[$scope]]#, ${domainObjectName}Service) {
			${domainObjectName}Service.get${domainClassName}s()
					.success(function(value){
				console.log(value);
				#[[$scope]]#.${domainClassName}s = value.rows;
				
				});
			});

	}	
)();
