package sk.janobono.quarkusnut.mapper;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import sk.janobono.quarkusnut.TestEnhancedRandomBuilder;
import sk.janobono.quarkusnut.domain.User;
import sk.janobono.quarkusnut.so.UserSO;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    public static EnhancedRandom enhancedRandom = TestEnhancedRandomBuilder.build();

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void userToUserSO() {
        User user = new User();
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
