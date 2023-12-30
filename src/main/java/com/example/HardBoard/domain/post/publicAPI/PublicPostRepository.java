package com.example.HardBoard.domain.post.publicAPI;

import com.example.HardBoard.domain.post.Category;
import com.example.HardBoard.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PublicPostRepository {

    Page<Post> findByCategoryWithoutBlockUser(Category category, List<Long> blockList, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    List<Post> findDayBestRecommendPostList(List<Long> blockUserIdList);

    List<Post> findWeekBestRecommendPostList(List<Long> blockUserIdList);

    List<Post> findMonthBestRecommendPostList(List<Long> blockUserIdList);

    List<Post> searchPosts(
            List<Long> blockList,
            Category category,
            SearchCriteria searchCriteria,
            SortCriteria sortCriteria,
            List<String> keywords,
            int page);
}
