package com.r2dbc.user.service;

import com.r2dbc.user.domain.User;
import com.r2dbc.user.dto.request.UserCreateRequest;
import com.r2dbc.user.dto.request.UserUpdateRequest;
import com.r2dbc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final R2dbcEntityTemplate template;


    public Mono<User> createUser(UserCreateRequest user) {
        return userRepository.save(User.toEntity(user));
    }

    public Mono<User> read(Long id) {
        return userRepository.findById(id);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /*
     * - 세밀한 제어가 필요한 경우 사용하기 좋다.
     * - Query와 Update 객체를 조합하여 특정 조건에 맞는 데이터만 부분적으로 업데이트할 수 있다.
     * - 이 방법은 엔티티 객체 전체를 불러와 수정하는 대신, 필요한 필드만 효율적으로 업데이트할 수 있다.
     * */
    public Mono<Long> templateUpdate(Long id, UserUpdateRequest request) {
        Update update = Update
                .update("name", request.name() == null ? "홍길동" : request.name())
                .set("email", request.email() == null ? "example@example.com" : request.email());
        return template.update(
                Query.query(Criteria.where("id").is(String.valueOf(id))),
                update,
                User.class
        );
    }

    /*
     * - Spring MVC의 더티 채킹 비슷하게 하는 방법이다(코드가 비슷하지 내부 동작이 비슷한 건 아니다)
     * - 엔티티를 조회한 다음 수정한 다음에 PK 값에 의존하여 update하는 방법이다.
     * */
    public Mono<User> repositoryUpdate(Long id, UserUpdateRequest request) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setName(request.name());
                    user.setEmail(request.email());
                    return userRepository.save(user);
                });
    }

    // ID로 삭제
    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id);
    }

    /*
     * - 복잡한 조건을 사용하여 데이터를 삭제할 때 사용한다.
     * - 특정 필드 값을 기준으로 여러 데이터를 한 번에 삭제해야 할 때 유용하다.
     * */
    public Mono<Long> deleteUsingTemplate(Long id) {
        return template.delete(
                Query.query(Criteria.where("id").is(String.valueOf(id))),
                User.class
        );
    }
}