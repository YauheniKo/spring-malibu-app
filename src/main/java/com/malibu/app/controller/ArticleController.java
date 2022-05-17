package com.malibu.app.controller;

import com.malibu.app.config.CurrentUser;
import com.malibu.app.dto.ArticleRequest;
import com.malibu.app.dto.ArticleResponse;
import com.malibu.app.dto.LocalUser;
import com.malibu.app.entity.Article;
import com.malibu.app.service.FileFirebaseService;
import com.malibu.app.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticle(@CurrentUser LocalUser user,
                                                               @RequestParam(required = false) String title) {
        return articleService.getAllArticle(title, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable("id") long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object createArticle(@RequestPart("article") ArticleRequest articleRequest,
                                @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return articleService.createArticle(articleRequest, files);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable("id") long id, @RequestBody Article article) {
        return articleService.updateArticle(id, article);//TODO change RequestBody on DTO
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArticle(@PathVariable("id") long id) {
        return articleService.deleteArticle(id);
    }

    @GetMapping("/like/{id}")
    public ResponseEntity<Long> gradeLike(@PathVariable("id") long articleId,
                                          @CurrentUser LocalUser user) {
        return articleService.gradeLike(articleId, user);
    }


}
