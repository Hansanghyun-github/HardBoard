package com.example.HardBoard.domain.comment;

import com.example.HardBoard.domain.BaseEntity;
import com.example.HardBoard.domain.post.Post;
import com.example.HardBoard.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "comments")
@ToString(exclude = {"user", "parent", "post"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent; // TODO jpa에 저장하기 전에 미리 저장 해놓으면, id도 같이 저장될까?

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "cnt_recommends")
    private Long cntRecommends;

    @Column(name = "cnt_unrecommends")
    private Long cntUnrecommends;

    @Builder
    public Comment(String contents, Post post, User user) {
        this.contents = contents;
        this.post = post;
        this.user = user;
        this.isDeleted = false;
        this.cntRecommends = 0L;
        this.cntUnrecommends = 0L;
    }

    public void setParent() {
        this.parent = this;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public void editContents(String contents) {
        this.contents = contents;
    }

    public void delete() {
        post.decreaseComment();
        isDeleted = true;
    }

    public static Comment create(String contents, Post post, User user){
        post.increaseCntComments();
        return Comment.builder()
                .contents(contents)
                .post(post)
                .user(user)
                .build();
    }

    public void recommend(){
        cntRecommends++;
    }

    public void cancelRecommend(){
        cntRecommends--;
    }

    public void unrecommend(){
        cntUnrecommends++;
    }

    public void cancelUnrecommend(){
        cntUnrecommends--;
    }
}
