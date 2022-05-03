package com.malibu.springmalibuapp.controller;

import com.malibu.springmalibuapp.model.Article;
import com.malibu.springmalibuapp.model.User;
import com.malibu.springmalibuapp.payload.request.ArticleRequest;
import com.malibu.springmalibuapp.security.services.UserDetailsImpl;
import com.malibu.springmalibuapp.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<Article>> getAllArticle(@RequestParam(required = false) String title) {
        return articleService.getAllArticle(title);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable("id") long id) {
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
