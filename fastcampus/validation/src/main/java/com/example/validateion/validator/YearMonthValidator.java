package com.example.validateion.validator;

import com.example.validateion.annotation.YearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class YearMonthValidator implements ConstraintValidator<YearMonth, String> {

    private String pattern;

    @Override
    public void initialize(YearMonth constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // yyyyMM01
        try {
            LocalDate localDate = LocalDate.parse(value + "01", DateTimeFormatter.ofPattern("yyyyMM"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
