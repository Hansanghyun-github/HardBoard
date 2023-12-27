package com.example.HardBoard.domain.block;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "blocks")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {
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

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Builder
    public Block(String comments, User user, User blockUser, LocalDateTime createdDateTime) {
        this.comments = comments;
        this.user = user;
        this.blockUser = blockUser;
        this.createdDateTime = createdDateTime;
    }
}
