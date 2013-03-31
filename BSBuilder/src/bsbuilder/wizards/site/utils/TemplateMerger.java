package bsbuilder.wizards.site.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class TemplateMerger {
	public static InputStream merge(String templateName, String projectName){
		Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init();
				
		VelocityContext context = new VelocityContext();		

		context.put( "projectName", projectName );

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
