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

    // TODO 굳이 글 하나당 추천 한개로 제한할 필요가 있을까? 하루당 추천 제한이면 충분하지 않을까?

    @Builder
    public PostRecommend(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
