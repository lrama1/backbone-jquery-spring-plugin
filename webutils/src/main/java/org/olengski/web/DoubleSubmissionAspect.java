package org.olengski.web;

import static org.reflections.ReflectionUtils.getFields;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class DoubleSubmissionAspect {
	
	//Map<Integer, Date> mapOfSubmittedData = new HashMap<Integer, Date>();
	
	Logger logger = Logger.getLogger(this.getClass());	
	//60000 is 1 minute
	private long DEFAULT_MAX_AGE = 60000;
	private long DEFUALT_MAX_HASH_LIST_SIZE = 15;	
	private long maxAge = 0;
	private long maxHashListSize = 0;
		
	
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerBean() {}
	
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void doubleSubmitProtected() {}
	
	//@Before("execution(public * *(..))&& execution(@com.oleng.web.DoubleSubmitProtect * *(..)) && controllerBean()")
	@Before("execution(public * *(..))&& execution(@org.olengski.web.DoubleSubmitProtect * *(..))")
    public void decorateForSecurity(JoinPoint joinPoint) throws Throwable {
		Object[] arguments = joinPoint.getArgs();
		StringWriter writer = new StringWriter();
		for(Object argument : arguments){
			//System.out.println(concatFields(writer, argument));
			concatFields(writer, argument);
		}
		String concatenatedString = writer.toString();
		logger.info(concatenatedString);
		Integer hashCode = concatenatedString.hashCode();
		if(maxAge  == 0){
			maxAge = DEFAULT_MAX_AGE;
		}
				    
		if(getCurrentSessionSubmissions().size() > maxAge){
			discardOldHashes();
		}
		Date existingHashAdded = getCurrentSessionSubmissions().get(hashCode); 
		if( existingHashAdded != null){
			Long age = System.currentTimeMillis() - existingHashAdded.getTime();
			if(age < maxAge){
				throw new RuntimeException("Double submission prevented. A similar submission was received before the threshold expired.");
			}else{
				getCurrentSessionSubmissions().put(hashCode, new Date());
			}
		}else{
			getCurrentSessionSubmissions().put(hashCode, new Date());
		}
		System.out.println("Final String: " + writer.toString());
	}
	
	protected Map<Integer, Date> getCurrentSessionSubmissions(){
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    Map<Integer, Date> mapOfSubmittedData = (Map)servletRequestAttributes.getRequest().getSession().getAttribute("USER_SUBMISSIONS");
	    if(mapOfSubmittedData == null){
	    	mapOfSubmittedData = new HashMap<Integer, Date>();
	    }
	    servletRequestAttributes.getRequest().getSession().setAttribute("USER_SUBMISSIONS", mapOfSubmittedData);
	    return mapOfSubmittedData;
	}
	
	/**
	 * A method which recursively inspects an Object and
	 * concatenates all values of all primitive attributes into a resulting String
	 * @param writer
	 * @param objectToEncode
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws Exception
	 */
	protected String concatFields(StringWriter writer, Object objectToEncode) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		logger.info("Atttempting to process " + objectToEncode.getClass().getName());
		if(objectToEncode != null && (isOfTypeWeCareAbout(objectToEncode))){
			logger.info("Adding: " + objectToEncode.getClass().getName());
			writer.append(ESAPI.encoder().canonicalize(objectToEncode.toString()));
		}else if(objectToEncode != null && objectToEncode instanceof List){
			//logger.info("Encountered a list/collection........................");			
			List<Object> listToProcess = (List<Object>)objectToEncode;
			for(Object object : listToProcess){
					concatFields(writer, object);				
			}
		}else if(objectToEncode != null && objectToEncode.getClass().isArray()){
			//logger.info("Encountered a list/collection........................");			
			Object[] listToProcess = (Object[])objectToEncode;
			for(Object object : listToProcess){
					concatFields(writer, object);				
			}
		}
		else if(objectToEncode != null && !(objectToEncode.getClass() == String.class)){			
			Set<Field> fields = getFields(objectToEncode.getClass());
			for(Field field : fields){	
				Set<Method> getterMethods =  getMethods(objectToEncode.getClass(), withName(getGetterMethodName(field)), withModifier(Modifier.PUBLIC));
				if(getterMethods.size() > 0){
					//logger.info("Processing complex object: " + field.getName());
					Method method = getterMethods.iterator().next();
					concatFields(writer, method.invoke(objectToEncode));	
				}
			}
		}
		return writer.toString();
	}
	
	private boolean isOfTypeWeCareAbout(Object object){
		return object.getClass() == String.class || object.getClass() == Integer.class ||
				object.getClass() == Long.class || object.getClass() == BigDecimal.class || 
				object.getClass() == Float.class ||
				object.getClass() == Double.class ||
				object.getClass() == Short.class ||
				object.getClass() == Byte.class ||
				object.getClass() == Date.class ||
				object instanceof HttpSession
				;
	}
	
	private String getGetterMethodName(Field field){
		return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}
	
	private String getSetterMethodName(Field field){
		return "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}
	
	private synchronized void discardOldHashes(){
		long currentTime = System.currentTimeMillis();
		logger.info("Cleaning up hashes: " + getCurrentSessionSubmissions().size());
		List<Integer> keysOfHashesToEvict = new ArrayList<Integer>();
		if(maxHashListSize == 0){
			maxHashListSize = DEFUALT_MAX_HASH_LIST_SIZE;
		}
		for(Integer key : getCurrentSessionSubmissions().keySet()){
			long age = currentTime - getCurrentSessionSubmissions().get(key).getTime();
			if(age >= maxHashListSize){
				logger.info("evicting " + age);								
				keysOfHashesToEvict.add(key);
			}
		}
		
		for(Integer key : keysOfHashesToEvict){
			getCurrentSessionSubmissions().remove(key);
		}		
		logger.info("After cleaning up tokens: " + getCurrentSessionSubmissions().size());
	}
		
	public long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(long maxAge) {
		this.maxAge = maxAge;
	}

	public long getMaxHashListSize() {
		return maxHashListSize;
	}

	public void setMaxHashListSize(long maxHashListSize) {
		this.maxHashListSize = maxHashListSize;
	}

	public static void main(String[] args){
		
		
	}
}
