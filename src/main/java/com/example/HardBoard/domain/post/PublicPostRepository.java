package com.example.HardBoard.domain.post;

import com.example.HardBoard.api.service.post.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicPostRepository {

    Page<Post> findByCategoryWithoutBlockUser(Category category, List<Long> blockList, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);
}
