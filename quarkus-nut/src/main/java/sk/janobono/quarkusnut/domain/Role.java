package sk.janobono.quarkusnut.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
@Table(name = "nut_role")
@SequenceGenerator(name = "role_generator", allocationSize = 1, sequenceName = "nut_role_seq")
public class Role {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "role_generator")
    private Long id;

    @Column(name = "name", length = 16, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
