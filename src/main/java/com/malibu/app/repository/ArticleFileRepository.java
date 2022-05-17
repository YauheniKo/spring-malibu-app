package com.malibu.app.repository;

import com.malibu.app.entity.Article;
import com.malibu.app.entity.ArticleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleFileRepository extends JpaRepository<ArticleFile, Long> {

    List<ArticleFile> findAllByArticleId(Long id);
}
