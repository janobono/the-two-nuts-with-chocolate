package sk.janobono.springbootnut.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.springbootnut.domain.Role;
import sk.janobono.springbootnut.domain.RoleName;
import sk.janobono.springbootnut.domain.User;
import sk.janobono.springbootnut.so.UserSO;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "roles", ignore = true)
    })
    UserSO userToUserSO(User user);

    @AfterMapping
    static void userToUserSO(User user, @MappingTarget UserSO userSO) {
        user.getRoles().forEach(role -> userSO.getRoles().add(role.getName().toString()));
    }

    @Mappings({
            @Mapping(target = "roles", ignore = true)
    })
    User userSOToUser(UserSO userSO);

    @AfterMapping
    static void userSOToUser(UserSO userSO, @MappingTarget User user) {
        userSO.getRoles().forEach(roleName -> {
            Role role = new Role();
            role.setName(RoleName.valueOf(roleName));
            user.getRoles().add(role);
        });
    }
}
