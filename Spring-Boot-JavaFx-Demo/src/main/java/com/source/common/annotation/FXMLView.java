package com.source.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface FXMLView {

	/**
	 * Value refers to a relative path from where to load a certain fxml file.
	 *
	 * @return the relative file path of a views fxml file.
	 */
	String value() default "";

	/**
	 * Css files to be used together with this view.
	 *
	 * @return the string[] listing all css files.
	 */
	String[] css() default {};

	/**
	 * Resource bundle to be used with this view.
	 *
	 * @return the string of such resource bundle.
	 */
	String bundle() default "";

	/**
	 * The encoding that will be sued when reading the {@link #bundle()} file.
	 * The default encoding is ISO-8859-1.
	 *
	 * @return  the encoding to use when reading the resource bundle
	 */
	String encoding() default "UTF-8";

	/**
	 * The default title for this view for modal.
	 *
	 * @return The default title string.
	 */
	String title() default "";

	/**
	 * The style to be applied to the underlying stage
	 * when using this view as a modal window.
	 */
	String stageStyle() default "UTILITY";
}