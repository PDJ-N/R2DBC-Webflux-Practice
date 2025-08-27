package com.todo.image;

import com.todo.exception.CustomException;
import com.todo.exception.dto.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ResourceLoader resourceLoader;
    private final ImagePathValidator imagePathValidator;
    private final String locationPattern = "classpath:img/";
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * 이미지명으로 이미지를 가져올 수 있는 메소드
     *
     * @param image 가져올 이미지의 파일명
     */
    public Mono<Resource> getImage(String image) {
        String fileName = Paths.get(image).getFileName().toString();

        // 유효하지 않은 요청이라면 오류를 발생시킴
        if (!imagePathValidator.isValidPath(fileName)) {
            return Mono.error(new CustomException(
                    ErrorMessage.INVALID_IMAGE_PATH,
                    "유효한 이미지 요청이 아닙니다.")
            );
        }

        Resource imageResource = resourceLoader.getResource(locationPattern + fileName);

        if (!imageResource.exists()) {
            // 파일을 찾지 못했을 경우 404 Not Found 에러를 반환
            return Mono.error(new CustomException(ErrorMessage.NOT_FOUND_IMAGE, "Image not found: " + fileName));
        }
        return Mono.just(imageResource);
    }

    /**
     * 모든 이미지를 버퍼에 담아서 가져오는 메소드
     */
    public Flux<DataBuffer> getAllImageByBuffer() {
        final Resource[] resources;

        // 이미지 리소스를 가져옴
        try {
            resources = resourcePatternResolver.getResources(locationPattern);
        } catch (IOException e) {
            return Flux.error(new CustomException(
                    ErrorMessage.INTERNAL_SERVER_ERROR,
                    "이미지 리소스 탐색 중 오류가 발생했습니다."
            ));
        }

        // 이미지를 찾지 못했을 시 예외를 발생하도록 함
        if (resources.length == 0) {
            return Flux.error(new CustomException(
                    ErrorMessage.NOT_FOUND_IMAGE,
                    "이미지를 찾지 못했습니다."
            ));
        }

        // 파일 단위 순차 전송으로 interleaving 방지
        return Flux.fromArray(resources)
                .concatMap(resource ->
                        DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024)
                );
    }

    /**
     * 애플리케이션에 저장된 이미지를 가져올 수 있는 REST API URL을 반환하는 메소드.
     *
     * @implNote 이미지가 세 개 밖에 없기 때문에 이대로 명시해둠
     */
    public Flux<String> getAllImagePath() {
        return Flux.just(
                "/api/images/webflux.png",
                "/api/images/docker.png",
                "/api/images/swagger.png"
        );
    }
}