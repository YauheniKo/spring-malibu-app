package com.malibu.app.service.article;

import com.malibu.app.dto.LocalUser;
import com.malibu.app.entity.Article;
import com.malibu.app.entity.ERole;
import com.malibu.app.entity.Like;
import com.malibu.app.entity.Role;
import com.malibu.app.entity.Tag;
import com.malibu.app.dto.ArticleRequest;
import com.malibu.app.dto.ArticleResponse;
import com.malibu.app.repository.ArticleRepository;
import com.malibu.app.repository.LikeRepository;
import com.malibu.app.repository.TagRepository;
import com.malibu.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
    private final LikeRepository likeRepository;

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

            articleResponses = articles.stream().map(article -> {
                long userId = userLocal.getUser().getId();
                long articleId = article.getId();

                return new ArticleResponse()
                        .setId(articleId)
                        .setUserId(userId)
                        .setUsername(article.getUser().getUsername())
                        .setTitle(article.getTitle())
                        .setDescription(article.getDescription())
                        .setText(article.getText())
                        .setTag(article.getTag())
                        .setLikes(likeRepository.countByArticle(article.getId()))
                        .setMeLiked(likeRepository.existsByArticleAndUsr(articleId, userId))
                        .setPublished(article.isPublished());
            }).collect(Collectors.toList());
            return new ResponseEntity<>(articleResponses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ArticleResponse> getArticleById(long id) {
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

    @Transactional
    public ResponseEntity<Long> createArticle(ArticleRequest articleRequest) {
        try {

            Article newArticle = articleRepository
                    .save(new Article()
                            .setTitle(articleRequest.getTitle())
                            .setDescription(articleRequest.getDescription())
                            .setText(articleRequest.getText())
                            .setTag(getTags(articleRequest.getTagName()))
                            .setCreateAt(new Date())
                            .setUser(userRepository.findById(articleRequest.getUserId()).get()));

            return new ResponseEntity<>(newArticle.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Article> updateArticle(long id, Article article) {

        Article updateArticle = articleRepository
                .findById(id)
                .map(a -> a.setTitle(article.getTitle())
                        .setDescription(article.getDescription())
                        .setText(article.getText())
                        .setTag(article.getTag())
                        .setPublished(article.isPublished())
                        .setUpdateAt(new Date()))
                .orElseThrow(() -> new EntityNotFoundException("Can't find Article with id: " + id));

        return new ResponseEntity<>(articleRepository.save(updateArticle), HttpStatus.OK);

    }


    public ResponseEntity<HttpStatus> deleteArticle(long id) {
        try {
            articleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Long> gradeLike(long articleId, LocalUser user) {
        Long userId = user.getUser().getId();

        Optional<Like> currentUserLike = likeRepository.findByUsrAndAndArticle(userId, articleId);
        if (currentUserLike.isPresent()) {
            likeRepository.deleteById(currentUserLike.get().getId());
        } else {
            likeRepository.save(new Like().setArticle(articleId)
                    .setUsr(userId));
        }
        return new ResponseEntity<>(likeRepository.countByArticle(articleId), HttpStatus.OK);
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
        return roleSet.contains(ERole.ROLE_ADMIN.name()) || roleSet.contains(ERole.ROLE_MODERATOR);

    }
}
