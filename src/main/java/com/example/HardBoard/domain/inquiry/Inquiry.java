package com.example.HardBoard.domain.inquiry;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "comments")
@ToString(exclude = {"user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "inquiry_id")
    private Long id;

    private String title;

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String response; // TODO 처음에는 null로 관리하지 말고 디폴트 값을 넣어줘야 한다

    @Builder
    public Inquiry(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
    }
}
