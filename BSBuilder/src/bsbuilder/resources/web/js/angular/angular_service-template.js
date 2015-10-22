#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
(
	function(){
	  var ${domainObjectName}Service = function(#[[$http, $routeParams]]#){
		  
		  //define service methods
		  this.get${domainClassName}s = function(){			  
			  return #[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}s',{params : {
					page : 1,
					per_page : 1000
				}});
		  };
		  
		  this.get${domainClassName} = function(id){
			return #[[$http]]#.get('/${projectName}/${domainClassName.toLowerCase()}/' + #[[$routeParams]]#.id);
		  };
		  
	  };
	  
	  angular.module('${projectName}').service('${domainObjectName}Service', ${domainObjectName}Service);
	}
());