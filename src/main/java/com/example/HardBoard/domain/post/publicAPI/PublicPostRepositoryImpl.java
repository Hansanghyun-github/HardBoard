package com.example.HardBoard.domain.post.publicAPI;

import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.HardBoard.domain.post.QPost.post;
import static com.example.HardBoard.domain.post.QPostRecommend.*;

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
        QueryResults<Post> results = queryFactory
                .select(post)
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

    @Override
    public List<Post> findDayBestRecommendPostList(List<Long> blockUserIdList) {
        LocalDateTime midnightYesterday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT)
                .minusDays(1L);
        LocalDateTime beforeMidnightToday = LocalDateTime.of(
                LocalDate.now(), LocalTime.MIDNIGHT)
                .minusSeconds(1L);

        return queryFactory
                .select(post)
                .from(postRecommend)
                .leftJoin(postRecommend.post, post)
                .where(post.createdDateTime.between(midnightYesterday, beforeMidnightToday),
                        post.user.id.notIn(blockUserIdList))
                .groupBy(postRecommend.post.id)
                .orderBy(postRecommend.count().desc())
                .limit(20L)
                .fetch();
    }

    @Override
    public List<Post> findWeekBestRecommendPostList(List<Long> blockUserIdList) {
        LocalDateTime midnightWeekAgo = LocalDateTime.of(
                        LocalDate.now(), LocalTime.MIDNIGHT)
                .minusWeeks(1L);
        LocalDateTime beforeMidnightToday = LocalDateTime.of(
                        LocalDate.now(), LocalTime.MIDNIGHT)
                .minusSeconds(1L);

        return queryFactory
                .select(post)
                .from(postRecommend)
                .leftJoin(postRecommend.post, post)
                .where(post.createdDateTime.between(midnightWeekAgo, beforeMidnightToday),
                        post.user.id.notIn(blockUserIdList))
                .groupBy(postRecommend.post.id)
                .orderBy(postRecommend.count().desc())
                .limit(20L)
                .fetch();
    }

    @Override
    public List<Post> findMonthBestRecommendPostList(List<Long> blockUserIdList) {
        LocalDateTime midnightMonthAgo = LocalDateTime.of(
                        LocalDate.now(), LocalTime.MIDNIGHT)
                .minusMonths(1L);
        LocalDateTime beforeMidnightToday = LocalDateTime.of(
                        LocalDate.now(), LocalTime.MIDNIGHT)
                .minusSeconds(1L);

        return queryFactory
                .select(post)
                .from(postRecommend)
                .leftJoin(postRecommend.post, post)
                .where(post.createdDateTime.between(midnightMonthAgo, beforeMidnightToday),
                        post.user.id.notIn(blockUserIdList))
                .groupBy(postRecommend.post.id)
                .orderBy(postRecommend.count().desc())
                .limit(20L)
                .fetch();
    }

    @Override
    public List<Post> searchPosts(
            List<Long> blockList,
            Category category,
            SearchCriteria searchCriteria,
            SortCriteria sortCriteria,
            List<String> keywords,
            int page) {
        return null;
    }

    private Predicate categoryEq(Category category) {
        return (category != Category.All) ? post.category.eq(category) : null;
    }
}
