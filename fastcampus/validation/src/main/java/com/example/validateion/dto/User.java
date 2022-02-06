package com.example.validateion.dto;

import com.example.validateion.annotation.YearMonth;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class User {

    private String name;

    private int age;

    @Email
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 양식와 맞지 않습니다. 01x-xxx(x)-xxxx")
    private String phoneNumber;

    @YearMonth
    private String reqYearMonth;  // yyyyMM

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReqYearMonth() {
        return reqYearMonth;
    }

    public void setReqYearMonth(String reqYearMonth) {
        this.reqYearMonth = reqYearMonth;
    }

    @AssertTrue(message = "yyyyMM 의 형식에 맞지 않습니다.")
    public boolean isReqYearMonthValidation() {
        this.reqYearMonth = getReqYearMonth() + "01";

        try {
            LocalDate localDate = LocalDate.parse(this.reqYearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
