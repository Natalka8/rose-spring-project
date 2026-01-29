package org.example.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    String value() default ""; // Custom action description
    boolean logArguments() default true; // Log method arguments
    boolean logResult() default true; // Log result
}