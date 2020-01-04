package sk.janobono.quarkusnut.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.janobono.quarkusnut.service.UserService;
import sk.janobono.quarkusnut.so.Page;
import sk.janobono.quarkusnut.so.Pageable;
import sk.janobono.quarkusnut.so.UserSO;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<UserSO> getUsers(@QueryParam("page") Integer page, @QueryParam("size") Integer size, @QueryParam("sort") String sort) {
        LOGGER.debug("getUsers({},{},{})", page, size, sort);
        Pageable pageable = new Pageable();
        pageable.setPageNumber(page);
        pageable.setPageSize(size);
        pageable.setSort(sort);
        return userService.getUsers(pageable);
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") Long id) {
        LOGGER.debug("getUser({})", id);
        if (userService.userExists(id)) {
            return Response.ok(userService.getUser(id)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("User not found!").build();
        }
    }

    @POST
    public Response addUser(@Valid UserSO userSO) {
        LOGGER.debug("addUser({})", userSO);
        return Response.status(Response.Status.CREATED).entity(userService.addUser(userSO)).build();
    }

    @PUT
    @Path("{id}")
    public Response setUser(@PathParam("id") Long id, @Valid UserSO userSO) {
        LOGGER.debug("setUser({},{})", id, userSO);
        if (userService.userExists(id)) {
            userSO.setId(id);
            return Response.ok(userService.setUser(userSO)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("User not found!").build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        LOGGER.debug("deleteUser({})", id);
        userService.deleteUser(id);
        return Response.ok().build();
    }
}
