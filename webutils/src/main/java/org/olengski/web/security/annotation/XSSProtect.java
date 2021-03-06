package org.olengski.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XSSProtect {
	boolean csrfProtect() default false;
	boolean xssProtect() default false;
	public EncodeType encodeFor() default EncodeType.HTML_ATTRIBUTE;
}
