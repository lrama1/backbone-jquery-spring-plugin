package bsbuilder.wizards.site.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class TemplateMerger {
	public static InputStream merge(String templateName, String projectName, String basePackageName, String controllerPackageName){
		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init();
				
		VelocityContext context = new VelocityContext();		

		context.put( "projectName", projectName );
		context.put( "basePackageName", basePackageName );
		context.put( "controllerPackageName", controllerPackageName );

		Template template = null;

		try
		{
		   template = Velocity.getTemplate(templateName);	
		}catch( Exception e )
		{
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();
		template.merge( context, sw );
		return new ByteArrayInputStream(sw.toString().getBytes());
	}
	
	public static InputStream merge(String templateName,String domainPackageName ,String className,
			String domainClassIdAttributeName ,Map<String, Object> attrs){
		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init();
				
		VelocityContext context = new VelocityContext();		

		context.put( "domainPackageName", domainPackageName );
		context.put( "className", className );
		context.put("domainClassIdAttributeName", domainClassIdAttributeName);
		context.put( "attrs", attrs );


		Template template = null;

		try
		{
		   template = Velocity.getTemplate(templateName);	
		}catch( Exception e )
		{
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();
		template.merge( context, sw );
		return new ByteArrayInputStream(sw.toString().getBytes());
	}
	
	
	
	public static InputStream merge(String templateName, Map<String, Object> valuesToPlug){
		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init();
				
		VelocityContext context = new VelocityContext();		
		for(String key : valuesToPlug.keySet()){
			context.put(key, valuesToPlug.get(key));
		}

		Template template = null;

		try
		{
		   template = Velocity.getTemplate(templateName);	
		}catch( Exception e )
		{
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();
		template.merge( context, sw );
		return new ByteArrayInputStream(sw.toString().getBytes());
	}
	
	public static InputStream mergeMap(String templateName, String domainClassName ,Map<String, Object> valuesToPlug){
		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init();
						
		VelocityContext context = new VelocityContext();	
		context.put("domainClassName", domainClassName);
		context.put("valuesToPlug", valuesToPlug);
		
		Template template = null;

		try
		{
		   template = Velocity.getTemplate(templateName);	
		}catch( Exception e )
		{
			e.printStackTrace();
		}

		StringWriter sw = new StringWriter();
		template.merge( context, sw );
		return new ByteArrayInputStream(sw.toString().getBytes());
	}
}
