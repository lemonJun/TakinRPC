package com.lemon.soa.common.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SPI Service
 * 
 * me
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SPI {

    /**
     * SPI value
     * 
     * @return
     */
    String value() default "default";

    /**
     * 排序顺序
     * 
     * @return
     */
    int order() default 0;

}
