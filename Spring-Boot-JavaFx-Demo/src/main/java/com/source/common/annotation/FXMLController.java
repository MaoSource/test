package com.source.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author  Source
 * @date  2022/4/7 17:03
 */

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface FXMLController {

}