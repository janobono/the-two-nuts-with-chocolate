package sk.janobono.springbootnut.mapper;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.janobono.springbootnut.TestEnhancedRandomBuilder;
import sk.janobono.springbootnut.domain.User;
import sk.janobono.springbootnut.so.UserSO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserMapperImpl.class})
public class UserMapperTest {

    private static EnhancedRandom enhancedRandom = TestEnhancedRandomBuilder.build();

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Test
    public void userToUserSO() {
        User user = enhancedRandom.nextObject(User.class);
        UserSO userSO = userMapper.userToUserSO(user);
        assertThat(user).isEqualToIgnoringGivenFields(userSO, "roles");
    }

    @Test
    public void userSOToUser() {
        UserSO userSO = enhancedRandom.nextObject(UserSO.class);
        User user = userMapper.userSOToUser(userSO);
        assertThat(userSO).isEqualToIgnoringGivenFields(user, "roles");
    }
}
