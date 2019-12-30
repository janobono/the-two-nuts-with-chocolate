package sk.janobono.springbootnut.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.springbootnut.service.UserService;
import sk.janobono.springbootnut.so.UserSO;

@RestController
@RequestMapping(value = "/user/")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserSO>> getUsers(Pageable pageable) {
        LOGGER.debug("getUsers({})", pageable);
        return new ResponseEntity<>(userService.getUsers(pageable), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserSO> getUser(@PathVariable("id") Long id) {
        LOGGER.debug("getUser({})", id);
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserSO> addUser(@RequestBody UserSO userSO) {
        LOGGER.debug("addUser({})", userSO);
        return new ResponseEntity<>(userService.addUser(userSO), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<UserSO> setUser(@PathVariable("id") Long id, @RequestBody UserSO userSO) {
        LOGGER.debug("setUser({},{})", id, userSO);
        userSO.setId(id);
        return new ResponseEntity<>(userService.setUser(userSO), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        LOGGER.debug("deleteUser({})", id);
        userService.deleteUser(id);
    }
}
