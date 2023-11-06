package com.example.HardBoard.domain.user;

import com.example.HardBoard.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id @PersistenceContext
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private Role role;

    @Builder
    private User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
