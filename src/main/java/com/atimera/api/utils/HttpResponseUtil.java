package com.atimera.api.utils;

import com.atimera.api.dto.HttpCustomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseUtil {

    public static void writeHttpResponse(HttpServletResponse response, HttpCustomResponse httpCustomResponse) throws IOException {
        // Écrire l'objet httpResponse dans la réponse
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpCustomResponse);
        outputStream.flush();
    }
}
