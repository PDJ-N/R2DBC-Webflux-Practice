package com.todo.controller;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import com.todo.image.ImagePathValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageSendController {

    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resourcePatternResolver;
    private final ImagePathValidator imagePathValidator;
    private final String locationPattern = "classpath:img/";

    /**
     * Resource를 사용한 정적 이미지 전송
     */
    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<Resource> getImage() {
        String imagePath = locationPattern + "webflux.png";

        // resourceLoader.getResource()는 존재하지 않는 파일도 Resource 객체를 반환하므로,
        // .exists()로 실제로 파일이 있는지 확인하는 것이 안전합니다.
        Resource imageResource = resourceLoader.getResource(imagePath);

        if (!imageResource.exists()) {
            // 파일을 찾지 못했을 경우 404 Not Found 에러를 반환
            return Mono.error(new CustomException(ErrorMessage.NOT_FOUND_IMAGE, "Image not found: " + imagePath));
        }

        return Mono.just(imageResource);
    }

    /**
     * /api/image/파일명 으로 URL을 받아서 이미지를 가져올 수 있도록 하는 REST API. 요청할 때 .png를 붙여야 한다. 안 그러면 오류남.
     *
     * @implNote 프로덕션 환경에서는 파일명이 아니라 데이터베이스에 조회 가능한 이미지의 ID 값을 받아서 조회해서 응답해야 한다. 프로덕션 환경이 아니고 간단히 구현하기 위해 이렇게 한 것.
     */
    @GetMapping(value = "/{image:.+\\.png}", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<Resource> getImage(@PathVariable String image) {
        String fileName = Paths.get(image).getFileName().toString();

        // 유효하지 않은 요청이라면 오류를 발생시킴
        if (!imagePathValidator.isValidPath(fileName)) {
            return Mono.error(new CustomException(
                    ErrorMessage.INVALID_IMAGE_PATH,
                    "유효한 이미지 요청이 아닙니다.")
            );
        }

        // resourceLoader.getResource()는 존재하지 않는 파일도 Resource 객체를 반환하므로,
        // .exists()로 실제로 파일이 있는지 확인하는 것이 안전합니다.
        Resource imageResource = resourceLoader.getResource(locationPattern + image);

        if (!imageResource.exists()) {
            // 파일을 찾지 못했을 경우 404 Not Found 에러를 반환
            return Mono.error(new CustomException(ErrorMessage.NOT_FOUND_IMAGE, "Image not found: " + fileName));
        }

        return Mono.just(imageResource);
    }

    /**
     * DataBuffer를 사용해서 스트림으로 데이터를 전송. 이미지 폴더에 있는 이미지를 모두 옥텟 스트림으로 보낸다.
     *
     * @implNote 프론트(클라이언트)에서 옥텟 스트리밍을 파싱할 수 있는 기능을 갖춰야한다.
     */
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<DataBuffer> streamImage() throws IOException {
        // ResourceLoader를 ResourcePatternResolver로 캐스팅하여 와일드카드를 지원하게 함
        Resource[] resources = resourcePatternResolver.getResources(locationPattern);

        // 찾은 모든 리소스를 Flux로 변환
        return Flux.fromArray(resources)
                .flatMap(resource -> {
                    // 각 리소스를 DataBuffer 스트림으로 읽어옴
                    return DataBufferUtils.read(
                            resource,
                            new DefaultDataBufferFactory(),
                            1024
                    );
                });
    }


    /**
     * 이런식으로 여러 개의 REST API URL을 보내면 클라이언트에서 이것으로 다시 요청을 보내도록 하는 것이다.
     *
     * @implNote 프론트(클라이언트)에서 SSE를 받는 로직을 따로 구현해야 한다.
     */
    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getImageUrls() {
        // 실제로는 데이터베이스나 파일 시스템에서 이미지 목록을 가져와서
        // Flux로 변환하여 보낼 수 있습니다.
        return Flux.just(
                "/api/images/webflux.png",
                "/api/images/docker.png",
                "/api/images/swagger.png"
        ).delayElements(Duration.ofSeconds(1)); // 예시를 위해 1초 간격으로 보냄
    }
}