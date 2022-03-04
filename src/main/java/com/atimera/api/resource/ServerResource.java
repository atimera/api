package com.atimera.api.resource;

import com.atimera.api.domain.Server;
import com.atimera.api.dto.HttpCustomResponse;
import com.atimera.api.exception.ExceptionHandling;
import com.atimera.api.services.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.atimera.api.enumeration.Status.SERVER_UP;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerResource extends ExceptionHandling {
    private final ServerService serverService;

    @GetMapping("/list")
    public ResponseEntity<HttpCustomResponse> getServers() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return ResponseEntity.ok(
                HttpCustomResponse.builder()
                        .timeStamp(new Date())
                        .data(of("servers", serverService.list(30)))
                        .message("Servers retrieved")
                        .httpStatus(OK)
                        .httpStatusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/ping/{ipAddress}")
    public ResponseEntity<HttpCustomResponse> pingServer(@PathVariable("ipAddress") String ipAddress) throws IOException {
        Server server = serverService.ping(ipAddress);
        return ResponseEntity.ok(
                HttpCustomResponse.builder()
                        .timeStamp(new Date())
                        .data(of("server", server))
                        .message(server.getStatus() == SERVER_UP ? "Ping success" : "Ping failed")
                        .httpStatus(OK)
                        .httpStatusCode(OK.value())
                        .build()
        );
    }

    @PostMapping("/save")
    public ResponseEntity<HttpCustomResponse> saveServer(@RequestBody @Valid Server server) {
        return ResponseEntity.ok(
                HttpCustomResponse.builder()
                        .timeStamp(new Date())
                        .data(of("server", serverService.create(server)))
                        .message("server created")
                        .httpStatus(CREATED)
                        .httpStatusCode(CREATED.value())
                        .build()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpCustomResponse> getServer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                HttpCustomResponse.builder()
                        .timeStamp(new Date())
                        .data(of("server", serverService.get(id)))
                        .message("server retrieved")
                        .httpStatus(OK)
                        .httpStatusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpCustomResponse> deleteServer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                HttpCustomResponse.builder()
                        .timeStamp(new Date())
                        .data(of("deleted", serverService.delete(id)))
                        .message("server deleted")
                        .httpStatus(OK)
                        .httpStatusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getServerImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "Downloads/images/" + fileName));
    }


}
