<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>

<title>sentiment</title>
<link rel="stylesheet" href="/sentiment/resources/js/libs/bootstrap.min.css" />
<link rel="stylesheet" href="/sentiment/resources/js/libs/bootstrap-theme.min.css" />
<link rel="stylesheet" href="/sentiment/resources/js/libs/backgrid.css" />
<link rel="stylesheet" href="/sentiment/resources/js/libs/backgrid-paginator.css" />
<link rel="stylesheet" href="/sentiment/resources/js/libs/datepicker.css" />
<link rel="stylesheet" href="/sentiment/resources/js/libs/backgrid-select-all.css" />
<link rel="stylesheet" href="/sentiment/resources/css/site.css" />
<script data-main="/sentiment/resources/js/main" src="/sentiment/resources/js/libs/require.js"></script>
<!-- COMMENT the preceeding and UNCOMMENT the succeeding lines if you are ready to deploy to a server, just before building -->
<!--script data-main="/sentiment/optimized-resources/main" src="/sentiment/resources/js/libs/require.js"></script-->
</head>
<body>
	<div class="container">		
		<div class="row">
			<div class="col-md-12">
				<nav class="navbar navbar-inverse" role="navigation">
					<div class="container-fluid">
						<!-- Brand and toggle get grouped for better mobile display -->
						<div class="navbar-header">
							<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
								<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span
									class="icon-bar"></span>
							</button>
							<a class="navbar-brand" href="#">Brand</a>
						</div>
		
						<!-- Collect the nav links, forms, and other content for toggling -->
						<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
							<ul class="nav navbar-nav">
								<li class="active"><a href="home">Home</a></li>
								<li><a href="#sentiments">Sentiments</a></li>
								<!-- MARKER FOR INSERTING -->
								<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
									<ul class="dropdown-menu">
										<li><a href="#">Action</a></li>
										<li><a href="#">Another action</a></li>
										<li><a href="#">Something else here</a></li>
										<li class="divider"></li>
										<li><a href="#">Separated link</a></li>
										<li class="divider"></li>
										<li><a href="#">One more separated link</a></li>
									</ul></li>
							</ul>
							<form class="navbar-form navbar-left" role="search">
								<div class="form-group">
									<input type="text" class="form-control" placeholder="Search">
								</div>
								<button type="submit" class="btn btn-default">Submit</button>
							</form>
							<ul class="nav navbar-nav navbar-right">
		       					 <li><a href="#">Logout</a></li>
		       				</ul>					
						</div>
						<!-- /.navbar-collapse -->
					</div>
					<!-- /.container-fluid -->
				</nav>
			</div>
		</div>	
		
		<div class="row">
			<div class="col-md-12" id="bodyContainer">
				<p>
					<spring:message code="welcome.message"/>
				</p>
			</div>
		</div>
	</div>
</body>
</html>