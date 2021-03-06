<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Sample JSP</title>
	
	<script type="text/javascript" src="<c:url value="resources/js/jquery-1.9.1.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="resources/js/underscore-min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="resources/js/backbone.js"/>"></script>
	<script type="text/javascript" src="<c:url value="resources/js/json2.js"/>"></script>
	<script type="text/javascript" src="<c:url value="resources/js/require.js"/>"></script>	
	<script type="text/javascript" src="<c:url value="resources/js/yourjs/components.js"/>"></script>	
	<!-- script type="text/javascript" src="<c:url value="resources/js/jquery.dataTables.js"/>"></script-->

	<script type="text/javascript">
		$(document).ready(function(){		
			//change underscore delims to using {{}}
			_.templateSettings = {
				evaluate :  /{{([\s\S]+?)}}/g,
				interpolate : /{{=([\s\S]+?)}}/g
			};
			
			var AppRouter = Backbone.Router.extend({
		        routes: {
		            "vehicle/:id": "getVehicle " // matches http://example.com/#/vehicle/{id}
		        }
		    });
		    // Initiate the router
		    var app_router = new AppRouter;
			var vehicleView = {};
		    
		    //handler for getting a Vehicle record
		    app_router.on('route:getVehicle', function (idToFetch) {
		        // Note the variable in the route definition being passed in here  
		        var vehicle  = new Vehicle({vin : idToFetch});			
				var result = vehicle.fetch({
					success : function(){
						//render the view when Vehicle is fetched successfully	
						vehicleView = new VehicleEditView({ el: $("#editContainer"), model : vehicle });
					},
					error : function(){
						alert("problem");
					}
				});
		    });
		    
		    // Start Backbone history a necessary step for bookmarkable URL's
		    Backbone.history.start();

		});
	</script>
  </head>
  
  <body>
  
    This is my JSP22 page. <br>
    <div id="editContainer">h</div>    
    
  </body>
</html>
