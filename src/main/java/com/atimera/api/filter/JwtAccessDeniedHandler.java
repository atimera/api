package com.atimera.api.filter;

import com.atimera.api.dto.HttpCustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.atimera.api.constant.SecurityConstantes.ACCESS_DENIED_MESSAGE;
import static com.atimera.api.utils.HttpResponseUtil.writeHttpResponse;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        HttpCustomResponse httpCustomResponse = HttpCustomResponse.builder()
                .httpStatus(UNAUTHORIZED)
                .httpStatusCode(UNAUTHORIZED.value())
                .message(ACCESS_DENIED_MESSAGE)
                .reason(UNAUTHORIZED.getReasonPhrase())
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());

        writeHttpResponse(response, httpCustomResponse);
    }
}
