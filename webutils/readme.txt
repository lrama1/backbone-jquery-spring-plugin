
Usages

Before moving on, make sure to have this repo in your pom:
<repositories>
    <repository>
      <id>sonatype-snapshots</id>
      <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
 </repositories>


=======================================
Double Submit

1.  Add Dependency
     <dependency>
         <groupId>org.olengski</groupId>
         <artifactId>webutils</artifactId>
         <version>0.0.1-SNAPSHOT</version>
     </dependency>

2.  Add bean to spring context
	<bean class="org.olengski.web.DoubleSubmissionAspect">		
		<property name="maxAge" value="60000"></property>
		<property name="maxHashListSize" value="50"></property>
	</bean>

    Or add the 'org.olengski.web' to the component scanning

3.  Annotate your controller method with '@DoubleSubmitProtect'


========================================
XSS Aspect

1.  Add Dependency
     <dependency>
         <groupId>org.olengski</groupId>
         <artifactId>webutils</artifactId>
         <version>0.0.1-SNAPSHOT</version>
     </dependency>

2.  Add bean to spring context
	<bean class="org.olengski.web.security.XSSAspect" />

3.  Annotate your controller method with '@XSSProtect'
	example: @XSSProtect(encodeFor = EncodeType.HTML_ATTRIBUTE)
	
================================================
CSRFHandlerInterceptor
1.  Add Dependency
     <dependency>
         <groupId>org.olengski</groupId>
         <artifactId>webutils</artifactId>
         <version>0.0.1-SNAPSHOT</version>
     </dependency>
2.  Add interceptor def
	<mvc:interceptors>          
        <bean class="org.olengski.web.security.interceptor.CSRFInterceptor"></bean>
	</mvc:interceptors>
3.  Annotate your controller method with @CSRFAssignToken (for methods that you want to generate the token, typically your GETs,
	But you can also add this to your PUT and POST in combination of @CSRFValidateToken so that it issues a new token after data is saved)
4.  Annotate your controller method with @CSRFValidateToken (for methods that you want to validate the token, typically your POSTs)
5.  On your client code, add something like the following to send/receive tokens:

		var tokens = [];	
		$.ajaxSetup({
		    beforeSend: function(xhr) {
		      xhr.setRequestHeader("CSRFToken",tokens.shift());    	    	
		    },
		    complete: function(xhr) {
		    	if(xhr.getResponseHeader("CSRFToken") != null){
		        	console.log(xhr.getResponseHeader("CSRFToken"));
		        	tokens.push(xhr.getResponseHeader("CSRFToken"));
		        }
		    }
		});
		
		//NOTE: An alternative is to put the tokens in a meta attribute of the HEAD.  That is if
				your application is not a Single-Page-App.
