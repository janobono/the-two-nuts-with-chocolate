package sk.janobono.springbootnut.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.janobono.springbootnut.BaseIntegrationTest;
import sk.janobono.springbootnut.domain.User;
import sk.janobono.springbootnut.mapper.UserMapper;
import sk.janobono.springbootnut.repository.UserRepository;
import sk.janobono.springbootnut.so.UserSO;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public UserMapper userMapper;

    @Test
    public void getUsers() throws Exception {
        for (int i = 0; i < 10; i++) {
            createRandomUser();
        }
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(paginatedUri("/user/", 0, 5, "username", Sort.Direction.ASC))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Page<UserSO> result = mapPagedResponse(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(result.getContent().size()).isEqualTo(5);
    }

    @Test
    public void getUser() throws Exception {
        User user = createRandomUser();
        String uri = "/user/" + user.getId();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO userSO = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(user).isEqualToIgnoringGivenFields(userSO, "roles");
    }

    @Test
    public void addUser() throws Exception {
        UserSO userSO = enhancedRandom.nextObject(UserSO.class, "id");
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(userSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        UserSO saved = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(userSO).isEqualToIgnoringGivenFields(saved, "id", "username");
        assertThat(userSO.getUsername().toLowerCase()).isEqualTo(saved.getUsername());
    }

    @Test
    public void setUser() throws Exception {
        User user = createRandomUser();
        UserSO userSO = userMapper.userToUserSO(user);
        userSO.setUsername("test0123");
        String uri = "/user/" + user.getId();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(userSO))).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        UserSO saved = mapFromJson(mvcResult.getResponse().getContentAsString(), UserSO.class);
        assertThat(userSO).isEqualToIgnoringGivenFields(saved);
    }

    @Test
    public void deleteUser() throws Exception {
        User user = createRandomUser();
        String uri = "/user/" + user.getId();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    private User createRandomUser() {
        User user = enhancedRandom.nextObject(User.class, "id");
        user.getRoles().clear();
        return userRepository.save(user);
    }
}
