package sk.janobono.quarkusnut.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import sk.janobono.quarkusnut.BaseIntegrationTest;
import sk.janobono.quarkusnut.domain.User;
import sk.janobono.quarkusnut.mapper.UserMapper;
import sk.janobono.quarkusnut.repository.UserRepository;
import sk.janobono.quarkusnut.so.Page;
import sk.janobono.quarkusnut.so.Pageable;
import sk.janobono.quarkusnut.so.UserSO;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class UserControllerTest extends BaseIntegrationTest {

    private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Inject
    UserRepository userRepository;

    @Test
    public void getUsers() throws Exception {
        for (int i = 0; i < 10; i++) {
            createRandomUser();
        }
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get(paginatedUri("/user/", 0, 5, "username", Pageable.SortDirection.ASC));
        assertThat(response.getStatusCode()).isEqualTo(javax.ws.rs.core.Response.Status.OK.getStatusCode());
        Page<UserSO> result = mapPagedResponse(response.body().asString(), UserSO.class);
        assertThat(result.getContent().size()).isEqualTo(5);
    }

    @Test
    public void getUser() throws Exception {
        User user = createRandomUser();
        String uri = "/user/" + user.getId();

        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get(uri);
        assertThat(response.getStatusCode()).isEqualTo(javax.ws.rs.core.Response.Status.OK.getStatusCode());
        UserSO userSO = mapFromJson(response.body().asString(), UserSO.class);
        assertThat(user).isEqualToIgnoringGivenFields(userSO, "roles");
    }

    @Test
    public void addUser() throws Exception {
        UserSO userSO = enhancedRandom.nextObject(UserSO.class, "id");
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.contentType(ContentType.JSON).body(mapToJson(userSO)).post("/user/");
        assertThat(response.getStatusCode()).isEqualTo(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
        UserSO saved = mapFromJson(response.body().asString(), UserSO.class);
        assertThat(userSO).isEqualToIgnoringGivenFields(saved, "id", "username");
        assertThat(userSO.getUsername().toLowerCase()).isEqualTo(saved.getUsername());
    }

    @Test
    public void setUser() throws Exception {
        User user = createRandomUser();
        UserSO userSO = userMapper.userToUserSO(user);
        userSO.setUsername("test0123");
        String uri = "/user/" + user.getId();
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.contentType(ContentType.JSON).body(mapToJson(userSO)).put(uri);
        assertThat(response.getStatusCode()).isEqualTo(javax.ws.rs.core.Response.Status.OK.getStatusCode());
        UserSO saved = mapFromJson(response.body().asString(), UserSO.class);
        assertThat(userSO).isEqualToIgnoringGivenFields(saved);
    }

    @Test
    public void deleteUser() {
        User user = createRandomUser();
        String uri = "/user/" + user.getId();
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.delete(uri);
        assertThat(response.getStatusCode()).isEqualTo(javax.ws.rs.core.Response.Status.OK.getStatusCode());
        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    private User createRandomUser() {
        UserSO userSO = enhancedRandom.nextObject(UserSO.class, "id", "roles");
        User user = userMapper.userSOToUser(userSO);
        userRepository.save(user);
        return user;
    }
}
