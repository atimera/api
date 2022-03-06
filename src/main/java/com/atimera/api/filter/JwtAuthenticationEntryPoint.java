package com.atimera.api.filter;

import com.atimera.api.dto.HttpCustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.atimera.api.constant.SecurityConstant.FORBIDDEN_MESSAGE;
import static com.atimera.api.utils.HttpResponseUtil.writeHttpResponse;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception)
            throws IOException {
        log.debug("Point d’entrée pré-authentifié appelé. Refus d’accès");
        HttpCustomResponse httpCustomResponse = HttpCustomResponse.builder()
                .httpStatus(FORBIDDEN)
                .httpStatusCode(FORBIDDEN.value())
                .message(FORBIDDEN_MESSAGE)
                .reason(FORBIDDEN.getReasonPhrase())
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());

        writeHttpResponse(response, httpCustomResponse);
    }


}
