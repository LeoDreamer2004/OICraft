package org.dindier.oicraft.model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

import static org.dindier.oicraft.assets.constant.ConfigConstants.SERVER_RESOURCE_FOLDER;
import static org.dindier.oicraft.assets.constant.ConfigConstants.SERVER_RESOURCE_URL;

/**
 * Basic user model
 */
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
    private String email;
    private String signature;
    private int experience = 0;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Temporal(TemporalType.DATE)
    private Date last_checkin;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Submission> submissions;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Problem> problems;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Post> posts;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Comment> comments;

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

    public boolean equals(User other) {
        if (other == null)
            return false;
        return this.id == other.id;
    }

    public String userAvatarFilePath() {
        return SERVER_RESOURCE_FOLDER + "/img/user/avatar/" + name;
    }

    public String userAvatarURL() {
        return SERVER_RESOURCE_URL + "/img/user/avatar/" + name;
    }

    public String defaultAvatarURL() {
        return SERVER_RESOURCE_URL + "/img/user/avatar/default_avatar.jpeg";
    }

    public boolean hasAvatar() {
        try {
            new URL(userAvatarURL());
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public String getAvatarPath() {
        return hasAvatar() ? userAvatarURL() : defaultAvatarURL();
    }
}
