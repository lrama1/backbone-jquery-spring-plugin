#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import VueResource from 'vue-resource'

<template>
	<div class="row">
		#foreach($key in $attrs.keySet() )
		<div class="form-group">
	    	<label for="${key}">${key}</label>
	    	<input type="text" class="form-control" id="${key}" v-model="${domainObjectName}.${key}">
	  	</div>
	  	#end 
	  	<div>
	  		<button type="button" class="btn btn-primary" @click="save${domainClassName}">Save</button>
	  		<button type="button" class="btn btn-primary" @click="update${domainClassName}">Update</button>
		</div>
	  	
	</div>
</template>

<script>
  export default {
    data () {
      let ${domainObjectName} = {
        firstName: '',
        lastName: ''
      }
      return {
        ${domainObjectName}
      }
    },
    created () {
      console.log(this.$route.params.id)
      if (this.$route.params.id) {
        console.log('found a value')
        var resource = this.$resource('/${projectName}/${domainObjectName}/' + this.$route.params.id)
        resource.query().then((response) => {
          this.${domainObjectName} = response.data
        }, (response) => {
        // error callback
          console.log('Error:' + response.statusText)
        })
      }
    },
    methods: {
      save${domainClassName} () {
        console.log(this.${domainObjectName})
        var resource = this.$resource('/${projectName}/${domainObjectName}')
        resource.save(this.${domainObjectName}).then((response) => {
        // success callback
        }, (response) => {
        // error callback
          console.log('Error:' + response.statusText)
        })
      },
      update${domainClassName} () {
        console.log(this.${domainObjectName})
        var resource = this.$resource('/${projectName}/${domainObjectName}')
        resource.update(this.${domainObjectName}).then((response) => {
        // success callback
        }, (response) => {
        // error callback
        })
      }
    }
  }
</script>