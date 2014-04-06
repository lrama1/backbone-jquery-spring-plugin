
Usages

=======================================
Double Submit

1.  Add Dependency
     <dependency>
         <groupId>org.oleng.web</groupId>
         <artifactId>webutils</artifactId>
         <version>0.0.1-SNAPSHOT</version>
     </dependency>

2.  Add bean to spring context
	<bean class="com.oleng.web.DoubleSubmissionAspect">		
		<property name="maxAge" value="60000"></property>
		<property name="maxHashListSize" value="50"></property>
	</bean>

    Or add the 'com.oleng.web' to the component scanning

3.  Annotate your controller method with '@DoubleSubmitProtect'


========================================
XSS Aspect

1.  Add Dependency
     <dependency>
         <groupId>org.oleng.web</groupId>
         <artifactId>webutils</artifactId>
         <version>0.0.1-SNAPSHOT</version>
     </dependency>

2.  Add bean to spring context
	<bean class="com.oleng.web.security.XSSAspect" />

3.  Annotate your controller method with '@XSSProtect'
	example: @XSSProtect(encodeFor = EncodeType.HTML_ATTRIBUTE)