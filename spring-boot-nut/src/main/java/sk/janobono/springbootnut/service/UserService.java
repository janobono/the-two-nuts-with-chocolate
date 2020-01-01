package sk.janobono.springbootnut.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sk.janobono.springbootnut.domain.RoleName;
import sk.janobono.springbootnut.domain.User;
import sk.janobono.springbootnut.mapper.UserMapper;
import sk.janobono.springbootnut.repository.RoleRepository;
import sk.janobono.springbootnut.repository.UserRepository;
import sk.janobono.springbootnut.so.UserSO;

@Service
@Transactional
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private ObjectMapper objectMapper;

    private KafkaProducer kafkaProducer;

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private UserMapper userMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Page<UserSO> getUsers(Pageable pageable) {
        LOGGER.debug("getUsers({})", pageable);
        Page<User> result = userRepository.findAll(pageable);
        LOGGER.debug("getUsers({})={}", pageable, result);
        return result.map(userMapper::userToUserSO);
    }

    public UserSO getUser(Long id) {
        LOGGER.debug("getUser({})", id);
        return userRepository.findById(id)
                .map(userMapper::userToUserSO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!"));
    }

    public UserSO addUser(UserSO userSO) {
        LOGGER.debug("addUser({})", userSO);
        if (userRepository.existsByEmail(userSO.getEmail().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with email exists!");
        }
        User user = userMapper.userSOToUser(userSO);
        user.getRoles().clear();
        userSO.getRoles().forEach(roleName -> {
            user.getRoles().add(roleRepository.findByName(RoleName.valueOf(roleName)));
        });
        UserSO result = userMapper.userToUserSO(userRepository.save(user));
        try {
            kafkaProducer.sendMessage(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public UserSO setUser(UserSO userSO) {
        LOGGER.debug("setUser({})", userSO);
        User user = userRepository.findById(userSO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!"));
        user.setUsername(userSO.getUsername());
        user.setEmail(userSO.getEmail());
        user.setEnabled(userSO.getEnabled());
        user.setLocked(userSO.getLocked());

        user.getRoles().clear();
        userSO.getRoles().forEach(roleName -> {
            user.getRoles().add(roleRepository.findByName(RoleName.valueOf(roleName)));
        });
        return userMapper.userToUserSO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        LOGGER.debug("deleteUser({})", id);
        userRepository.deleteById(id);
    }
}
