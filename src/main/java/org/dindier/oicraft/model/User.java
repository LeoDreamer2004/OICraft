package org.dindier.oicraft.model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "User",
        uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "username")
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Grade grade;

    private int experience = 0;

    @Temporal(TemporalType.DATE)
    private Date last_checkin;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Submission> submissions;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Problem> problems;

    protected User() {
    }

    public User(String name, String password, Role role, Grade grade) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.grade = grade;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ is a prefix for the role
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Role {
        ADMIN, USER
    }

    public enum Grade {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    public String getGradeString() {
        return grade.toString();
    }

    @SuppressWarnings("unused")
    public void load() {
        List<Submission> _temp = this.submissions;
    }

    public boolean equals(User other) {
        return this.id == other.id;
    }
}
