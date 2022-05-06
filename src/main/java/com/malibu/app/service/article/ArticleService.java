package com.malibu.app.service.article;

import com.malibu.app.dto.LocalUser;
import com.malibu.app.entity.Article;
import com.malibu.app.entity.ERole;
import com.malibu.app.entity.Role;
import com.malibu.app.entity.Tag;
import com.malibu.app.entity.User;
import com.malibu.app.payload.request.ArticleRequest;
import com.malibu.app.payload.response.ArticleResponse;
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

    public ResponseEntity<List<ArticleResponse>> getAllArticle(String title, LocalUser userLocal) {
        try {
            List<ArticleResponse> articleResponses;
            List<Article> articles;
            if (StringUtils.isEmpty(title)) {
                if (isAdminOrModer(userLocal)) {
                    articles = articleRepository.findAll();
                } else {
                    articles = articleRepository.findAllByPublishedIsTrue();
                }
            } else {
                if (isAdminOrModer(userLocal)) {
                    articles = articleRepository.findByTitle(title);
                } else {
                    articles = articleRepository.findByTitleAndPublishedIsTrue(title);
                }
            }

            if (articles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            articleResponses = articles.stream().map(article -> new ArticleResponse()
                    .setId(article.getId())
                    .setUserId(article.getUser().getId())
                    .setUsername(article.getUser().getUsername())
                    .setTitle(article.getTitle())
                    .setDescription(article.getDescription())
                    .setText(article.getText())
                    .setTag(article.getTag())
                    .setPublished(article.isPublished())).collect(Collectors.toList());
            return new ResponseEntity<>(articleResponses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable("id") long id) {
        Optional<Article> articleData = articleRepository.findById(id);
        ArticleResponse articleResponse = new ArticleResponse();
        return articleData.map(article -> {
                    articleResponse
                            .setId(article.getId())
                            .setUserId(article.getUser().getId())
                            .setUsername(article.getUser().getUsername())
                            .setTitle(article.getTitle())
                            .setDescription(article.getDescription())
                            .setText(article.getText())
                            .setTag(article.getTag())
                            .setPublished(article.isPublished());
                    return new ResponseEntity<>(articleResponse, HttpStatus.OK);
                })
                .orElse(
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
                    .setPublished(article.isPublished())
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

    private boolean isAdminOrModer(LocalUser localUser) {
        Set<String> roleSet = localUser
                .getUser()
                .getRoles()
                .stream().map(Role::getName).collect(Collectors.toSet());
        if (roleSet.contains(ERole.ROLE_ADMIN.name()) || roleSet.contains(ERole.ROLE_MODERATOR)) {

            return true;
        } else {
            return false;
        }

    }
}
