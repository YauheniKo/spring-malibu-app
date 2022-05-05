package com.malibu.app.service.article;

import com.malibu.app.model.Article;
import com.malibu.app.model.Tag;
import com.malibu.app.payload.request.ArticleRequest;
import com.malibu.app.repository.ArticleRepository;
import com.malibu.app.repository.TagRepository;
import com.malibu.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
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
    private final UserRepository userRepository;

    public ResponseEntity<List<Article>> getAllArticle(String title) {
        try {
            List<Article> articles;
            if (StringUtils.isEmpty(title)) {
                articles = articleRepository.findAll();
            } else {
                articles = articleRepository.findByTitle(title);
            }

            if (articles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Article> getArticleById(@PathVariable("id") long id) {
        Optional<Article> articleData = articleRepository.findById(id);

        return articleData.map(article ->
                new ResponseEntity<>(article, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequest articleRequest) {
        try {

            Article newArticle = articleRepository
                    .save(new Article()
                            .setTitle(articleRequest.getTitle())
                            .setDescription(articleRequest.getDescription())
                            .setText(articleRequest.getText())
                            .setTag(getTags(articleRequest.getTagName()))
                            .setCreateAt(new Date())
                            .setUser(userRepository.findById(articleRequest.getUserId()).get()));

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

    private Set<Tag> getTags(String tagNamesStr) {

        Set<String> tagNames = Arrays.stream(tagNamesStr.split(",")).collect(Collectors.toSet());

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