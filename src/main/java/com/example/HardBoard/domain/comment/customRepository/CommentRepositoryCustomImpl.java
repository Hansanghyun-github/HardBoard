package com.example.HardBoard.domain.comment.customRepository;

import com.example.HardBoard.domain.comment.Comment;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.HardBoard.domain.comment.QComment.*;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public CommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Comment> findByPostId(Long postId, List<Long> blockUserIdList, Pageable pageable) {
        QueryResults<Comment> results = queryFactory.
                select(comment)
                .from(comment)
                .where(comment.user.id.notIn(blockUserIdList))
                .orderBy(comment.createdDateTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Comment> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}
