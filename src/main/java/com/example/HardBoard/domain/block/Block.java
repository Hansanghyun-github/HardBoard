package com.example.HardBoard.domain.block;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "blocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "block_id")
    private Long id;

    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_user_id")
    private User blockUser;

    @Builder
    public Block(String comments, User user, User blockUser) {
        this.comments = comments;
        this.user = user;
        this.blockUser = blockUser;
    }
}
