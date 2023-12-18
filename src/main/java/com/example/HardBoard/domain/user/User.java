package com.example.HardBoard.domain.user;

import com.example.HardBoard.api.service.user.request.UserCreateServiceRequest;
import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.block.Block;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString()
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    // TODO 하루 추천 개수를 제한하고 싶다

    @Builder
    private User(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public static User create(String email, String password, String nickname, Role role){
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(role)
                .build();
    }

    public void changeNickname(String nickname){
        this.nickname = nickname;
    }

    public User checkPassword(PasswordEncoder passwordEncoder, String password){
        if(!passwordEncoder.matches(password, this.password))
            throw new IllegalArgumentException("Invalid password");
        return this;
    }

    public void changePassword(PasswordEncoder passwordEncoder, String password){ this.password = passwordEncoder.encode(password); }

    public List<Role> getRoleList() {
        return Arrays.asList(Role.values());
    }
}
