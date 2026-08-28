package com.barbup.barbup_api.domain.barbershop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Pattern(
        regexp = "^(\\d{8}|\\d{5}-\\d{3})$",
        message = "Zipcode field is invalid"
)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Zipcode {
    String message() default "Zipcode field is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
