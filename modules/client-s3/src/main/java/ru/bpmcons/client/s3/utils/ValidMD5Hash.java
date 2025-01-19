package ru.bpmcons.client.s3.utils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidMD5Hash.Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMD5Hash {
    String message() default "Invalid MD5 hash";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidMD5Hash, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            return MD5Utils.isValidHash(value);
        }
    }
}
