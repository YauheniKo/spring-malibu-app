package com.malibu.springmalibuapp.service.article;

import com.malibu.springmalibuapp.dto.request.ArticleRequestDto;
import com.malibu.springmalibuapp.model.Article;
import com.malibu.springmalibuapp.model.Tag;
import com.malibu.springmalibuapp.repository.ArticleRepository;
import com.malibu.springmalibuapp.repository.TagRepository;
import com.malibu.springmalibuapp.repository.UserRepository;
import com.malibu.springmalibuapp.security.services.UserDetailsImpl;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    public ResponseEntity<List<Article>> getAllArticle(@RequestParam(required = false) String title) {
        try {
            List<Article> articles = new ArrayList<>();
            if (title == null) {
                articles.addAll(articleRepository.findAll());
            } else {
                articles.addAll(articleRepository.findByTitleContaining(title));
            }

            if (articles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Article> getArticleById(@PathVariable("id") long id) {
        Optional<Article> articleData = articleRepository.findById(id);

        return articleData.map(article ->
                new ResponseEntity<>(article, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequestDto articleRequestDto) {
        try {

            Article newArticle = articleRepository
                    .save(new Article()
                            .setTitle(articleRequestDto.getTitle())
                            .setDescription(articleRequestDto.getDescription())
                            .setText(articleRequestDto.getText())
                            .setTag(getTags(articleRequestDto.getTagName()))
                            .setCreateAt(new Date()));

            return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Article> updateArticle(@PathVariable("id") long id,
                                                 @RequestBody Article article) {
        Optional<Article> articleData = articleRepository.findById(id);

        if (articleData.isPresent()) {
            Article newArticle = articleData
                    .get()
                    .setTitle(article.getTitle())
                    .setDescription(article.getDescription())
                    .setText(article.getText())
                    .setTag(article.getTag())
                    .setUpdateAt(new Date());

            return new ResponseEntity<>(articleRepository.save(newArticle), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<HttpStatus> deleteArticle(@PathVariable("id") long id) {
        try {
            articleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Set<Tag> getTags(Set<String> tagNames) {



        List<Tag> currentTag =
                tagNames
                        .stream()
                        .map(tagRepository::findTagByName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<String> newTagName =
                tagNames
                        .stream()
                        .filter(newName -> currentTag
                                .stream()
                                .noneMatch(currTg -> Objects.equals(currTg.getName(), newName)))
                        .collect(Collectors.toList());


        List<Tag> newTags = tagRepository.saveAll(newTagName
                .stream()
                .map(res -> new Tag().setName(res))
                .collect(Collectors.toList()));

        Set<Tag> newSetTag = new HashSet<>(newTags);
        newSetTag.addAll(currentTag);
        return newSetTag;


    }
}
