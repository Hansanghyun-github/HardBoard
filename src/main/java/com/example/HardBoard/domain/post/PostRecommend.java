package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend_post")
@ToString
public class PostRecommend {
    @Id @GeneratedValue
    @Column(name = "recommend_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostRecommend(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
