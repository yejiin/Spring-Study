package com.example.exception.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class User {

    @NotEmpty
    @Size(min = 1, max = 10)
    private String name;

    @Min(1)
    @NotNull
    private Integer age;
}
