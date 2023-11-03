package com.example.HardBoard.domain.notice;

import com.example.HardBoard.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "notices")
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    private String title;

    private String contents;

    @Builder
    private Notice(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public static Notice create(String title, String contents){
        return Notice.builder()
                .title(title)
                .contents(contents)
                .build();
    }
}
