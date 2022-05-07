package com.malibu.app.repository;

import com.malibu.app.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByTitle(String title);
    List<Article> findByTitleAndPublishedIsTrue(String title);
    List<Article> findAllByPublishedIsTrue();
}

