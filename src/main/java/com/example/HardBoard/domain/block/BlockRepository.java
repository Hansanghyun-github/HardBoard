package com.example.HardBoard.domain.block;

import com.example.HardBoard.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    void deleteByUserAndBlockUser(User user, User blockUser);
}
