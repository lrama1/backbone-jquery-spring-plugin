package com.oleng.web.security;

import static org.reflections.ReflectionUtils.getFields;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;

import com.oleng.web.security.annotation.EncodeType;
import com.oleng.web.security.annotation.UnsecuredField;
import com.oleng.web.security.annotation.XSSProtect;

@Aspect
@Component
public class XSSAspect {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Around("execution(@com.oleng.web.security.annotation.XSSProtect * *(..))")
    public Object decorateForSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] arguments = joinPoint.getArgs();			
		
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		EncodeType encodeType = method.getAnnotation(XSSProtect.class).encodeFor();
		
		//for XSS
		Object objectToReturn = joinPoint.proceed(arguments);
		encodeFields(objectToReturn, encodeType);			
		
		return objectToReturn;
	}
	
	/**
	 * Recursively inspects any object and encodes String fields accordingly
	 * @param objectToEncode
	 * @throws Exception
	 */
	private void encodeFields(Object objectToEncode, EncodeType encodeType) throws Exception{
		//logger.info("Atttempting to process " + objectToEncode.getClass().getSimpleName());
		if(objectToEncode != null && objectToEncode instanceof List){
			//logger.info("Encountered a list/collection........................");			
			List<Object> listToProcess = (List<Object>)objectToEncode;
			for(Object object : listToProcess){
				encodeFields(object, encodeType);
			}
		}
		else if(objectToEncode != null){			
			Set<Field> fields = getFields(objectToEncode.getClass());
			for(Field field : fields){
				if(field.getType() == String.class){		
					Set<Method> getterMethods =  getMethods(objectToEncode.getClass(), withName(getGetterMethodName(field)), withModifier(Modifier.PUBLIC));
					//need to access the value via its Getter Method
					if(getterMethods.size() > 0){						
						Object valueToEncode = getterMethods.iterator().next().invoke(objectToEncode);
						Set<Method> setterMethods =  getMethods(objectToEncode.getClass(), withName(getSetterMethodName(field)), withModifier(Modifier.PUBLIC));
						if(setterMethods.size() > 0){
							Method method = setterMethods.iterator().next();
							if(valueToEncode != null){
								String encoded = "";
								if(field.getAnnotation(UnsecuredField.class) == null){
									encoded = encodeField(field, valueToEncode.toString(), encodeType);
								}
								logger.info("Securing " + objectToEncode.getClass().getSimpleName()+ "." + field.getName() + "==============" + valueToEncode + " ==> " + encoded);								
								method.invoke(objectToEncode, encoded);
							}
						}
					}
					
				}else{
					Set<Method> getterMethods =  getMethods(objectToEncode.getClass(), withName(getGetterMethodName(field)), withModifier(Modifier.PUBLIC));
					if(getterMethods.size() > 0){
						//logger.info("Processing complex object: " + field.getName());
						Method method = getterMethods.iterator().next();
						encodeFields(method.invoke(objectToEncode), encodeType);	
					}					
				}
			}
		}
	}
	
	private String encodeField(Field field, String valueToEncode, EncodeType encodeType){	
		valueToEncode = ESAPI.encoder().canonicalize(valueToEncode);
		logger.info("ENCODE TYPE: " + encodeType.toString());
		if (encodeType.equals(EncodeType.HTML.toString())){		
			return ESAPI.encoder().encodeForHTML(valueToEncode);
		}else if (encodeType.equals(EncodeType.HTML_ATTRIBUTE)){	
			return ESAPI.encoder().encodeForHTMLAttribute(valueToEncode);
		}else if (encodeType.equals(EncodeType.JAVASCRIPT)){		
			return ESAPI.encoder().encodeForJavaScript(valueToEncode);
		}  
		return valueToEncode;
	}
	
	
	private String getGetterMethodName(Field field){
		return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}
	
	private String getSetterMethodName(Field field){
		return "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}

}
