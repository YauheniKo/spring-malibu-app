package com.malibu.app.controller;

import com.malibu.app.config.CurrentUser;
import com.malibu.app.dto.LocalUser;
import com.malibu.app.entity.Article;
import com.malibu.app.payload.request.ArticleRequest;
import com.malibu.app.payload.response.ArticleResponse;
import com.malibu.app.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticle(@CurrentUser LocalUser user, @RequestParam(required = false) String title) {
        return articleService.getAllArticle(title,user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable("id") long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle( @RequestBody ArticleRequest articleRequest) {
        return articleService.createArticle(articleRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable("id") long id, @RequestBody Article article) {
        return articleService.updateArticle(id, article);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArticle(@PathVariable("id") long id) {
        return articleService.deleteArticle(id);
    }

}
