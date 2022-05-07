package com.malibu.app.repository;

import com.malibu.app.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

     Optional<Like> findByUsrAndAndArticle(Long userId, Long articleAd);

     Long countByArticle(Long articleId);

     Boolean existsByArticleAndUsr(Long articleId, Long userId);
}
