package com.example.HardBoard.domain.post;

import com.example.HardBoard.domain.comment.Comment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.HardBoard.domain.comment.QComment.comment;
import static com.example.HardBoard.domain.post.QPost.*;

@Repository
public class PublicPostRepositoryImpl implements PublicPostRepository{
    private final JPAQueryFactory queryFactory;

    public PublicPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findByCategoryWithoutBlockUser(Category category, List<Long> blockUserIdList, Pageable pageable) {
        QueryResults<Post> results = queryFactory.
                select(post)
                .from(post)
                .where(post.user.id.notIn(blockUserIdList),
                        categoryEq(category))
                .orderBy(post.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Post> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Post> findByUserId(Long userId, Pageable pageable) {
        QueryResults<Post> results = queryFactory.
                select(post)
                .from(post)
                .where(post.user.id.eq(userId))
                .orderBy(post.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Post> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private Predicate categoryEq(Category category) {
        return (category != Category.All) ? post.category.eq(category) : null;
    }
}
