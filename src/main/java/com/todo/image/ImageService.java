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
     * */
    public Mono<Resource> getImage(String image) {
        String fileName = Paths.get(image).getFileName().toString();

        // 유효하지 않은 요청이라면 오류를 발생시킴
        if (!imagePathValidator.isValidPath(fileName)) {
            return Mono.error(new CustomException(
                    ErrorMessage.INVALID_IMAGE_PATH,
                    "유효한 이미지 요청이 아닙니다.")
            );
        }

        Resource imageResource = resourceLoader.getResource(locationPattern + image);

        if (!imageResource.exists()) {
            // 파일을 찾지 못했을 경우 404 Not Found 에러를 반환
            return Mono.error(new CustomException(ErrorMessage.NOT_FOUND_IMAGE, "Image not found: " + fileName));
        }
        return Mono.just(imageResource);
    }

    /**
     * 모든 이미지를 버퍼에 담아서 가져오는 메소드
     * */
    public Flux<DataBuffer> getAllImageByBuffer() throws IOException {
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
     * 애플리케이션에 저장된 이미지를 가져올 수 있는 REST API URL을 반환하는 메소드.
     *
     * @implNote 이미지가 세 개 밖에 없기 때문에 이대로 명시해둠
     * */
    public Flux<String> getAllImagePath() {
        return Flux.just(
                "/api/images/webflux.png",
                "/api/images/docker.png",
                "/api/images/swagger.png"
        );
    }
}