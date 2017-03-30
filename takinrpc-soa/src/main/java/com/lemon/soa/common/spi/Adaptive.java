package com.lemon.soa.common.spi;

/**
 * 扩展点适配器
 * 
 * me
 * @version v1.0
 */
public @interface Adaptive {

    String value() default "default";

}
