package com.todo.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/image")
public class ImageSendController {

    private final Path imageDir = Paths.get("src/main/resources/img");

    /**
     * Resource를 사용한 정적 이미지 전송
     */
    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<Resource> getImage() {
        return Mono.just(new ClassPathResource("/img/webflux.png"));
    }

    /**
     * DataBuffer를 사용해서 스트림으로 데이터를 전송
     *
     * @implNote 프론트(클라이언트)에서 옥텟 스트리밍을 파싱할 수 있는 기능을 갖춰야한다.
     * */
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<DataBuffer> streamImage() throws IOException {
        return Flux.fromStream(Files.walk(imageDir))
                .filter(path -> path.toString().endsWith(".png"))
                .flatMap(path -> DataBufferUtils.read(
                        path,
                        new DefaultDataBufferFactory(),
                        1024
                ));
    }
}