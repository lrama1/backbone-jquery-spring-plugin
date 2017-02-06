#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
<template>
  <div id="wrapper">
    <nav class="navbar navbar-default">
      <div class="container">
        <a class="navbar-brand" href="#">
          <i class="glyphicon glyphicon-time"></i>
          Template Application
        </a>
        <ul class="nav navbar-nav">
          <li><router-link to='/home'>Home</router-link></li>
          <li><router-link to='/${domainObjectName}s'>${domainClassName}s List</router-link></li>
        </ul>
      </div>
    </nav>
    <div class="container">
      <div class="col-sm-3">

      </div>
      <div class="col-sm-9">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>