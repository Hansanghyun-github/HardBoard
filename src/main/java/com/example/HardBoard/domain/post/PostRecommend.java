package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "post_recommends",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})})
@ToString
public class PostRecommend {
    @Id @GeneratedValue
    @Column(name = "post_recommend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostRecommend(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public static PostRecommend create(User user, Post post){
        post.recommend();
        return PostRecommend.builder()
                .user(user)
                .post(post)
                .build();
    }
}
