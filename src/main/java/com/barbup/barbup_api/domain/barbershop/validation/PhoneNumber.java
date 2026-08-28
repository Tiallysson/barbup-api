package com.barbup.barbup_api.domain.barbershop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Pattern(
        regexp = "^(\\d{2}[2-5]\\d{7}|\\d{2}9\\d{8})$",
        message = "Phone field is invalid"
)
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Phone field is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
