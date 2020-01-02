package sk.janobono.quarkusnut.so;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class UserSO {

    private Long id;

    @NotEmpty
    @Size(max = 255)
    private String username;

    @NotEmpty
    @Size(max = 255)
    @Email
    private String email;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean locked;

    private Set<String> roles;

    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }
}
