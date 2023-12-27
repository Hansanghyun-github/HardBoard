package com.example.HardBoard.domain.inquiry;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "inquiries")
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

    @Column(name = "is_responded")
    private Boolean isResponded;

    private String response;

    @Column(name = "respond_date_time")
    private LocalDateTime respondDateTime;

    @Builder
    public Inquiry(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.response = "default";
        this.isResponded = false;
    }

    public void edit(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void respond(String response, LocalDateTime respondDateTime) {
        this.response = response;
        this.respondDateTime = respondDateTime;
        this.isResponded = true;
    }
}
