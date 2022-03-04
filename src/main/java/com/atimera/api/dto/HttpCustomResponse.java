package com.atimera.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@SuperBuilder
@JsonInclude(NON_NULL) // si un champ est null on ne le montre pas dans le json
public class HttpCustomResponse {
    @Builder.Default
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss",
            timezone = "Europe/Paris")
    //timezone = JsonFormat.DEFAULT_LOCALE)
    protected Date timeStamp = new Date();
    protected int httpStatusCode;
    protected HttpStatus httpStatus;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;
}
