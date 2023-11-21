package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "unrecommend_post")
public class PostUnrecommend {
    @Id @GeneratedValue
    @Column(name = "unrecommend_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostUnrecommend(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
