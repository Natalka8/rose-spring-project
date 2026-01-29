// src/main/java/org/example/annotation/Loggable.java
package org.example.annotation;

import java.lang.annotation.*;

/**
 * Annotation for marking methods that should be logged
 * Used by the LoggingAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Loggable {

    /**
     * Logging detail level
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * Whether to log method parameters
     */
    boolean logParameters() default true;

    /**
     * Whether to log method result
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default true;

    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}