package com.abc.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OpenAccountRequest {
    @NotBlank
    private String owner;
    @Min(value = 0)
    private Double depositAmount;
    @Min(value = 0)
    private Double creditLine;
}
