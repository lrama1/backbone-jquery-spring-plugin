
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
CSRFHandlerIntercepto
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
3.  Annotate your controller method with @CSRFAssignToken (for methods that you want to generate the token, typically your GETs)
4.  Annotate your controller method with @CSRFValidateToken (for methods that you want to validate the token, typically your POSTs)