package com.malibu.app.service.article;

import com.malibu.app.dto.ArticleRequest;
import com.malibu.app.dto.ArticleResponse;
import com.malibu.app.dto.FireBaseResponseDto;
import com.malibu.app.dto.LocalUser;
import com.malibu.app.entity.Article;
import com.malibu.app.entity.ArticleFile;
import com.malibu.app.entity.ERole;
import com.malibu.app.entity.Like;
import com.malibu.app.entity.Role;
import com.malibu.app.entity.Tag;
import com.malibu.app.mappers.ArticleMapper;
import com.malibu.app.repository.ArticleFileRepository;
import com.malibu.app.repository.ArticleRepository;
import com.malibu.app.repository.LikeRepository;
import com.malibu.app.repository.TagRepository;
import com.malibu.app.repository.UserRepository;
import com.malibu.app.service.FileFirebaseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private final FileFirebaseService fileService;
    private final ArticleFileRepository articleFileRepository;
    private final ArticleMapper articleMapper;

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
                    articles = articleRepository.findByTitleContains(title);
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

                ArticleResponse newResponse = new ArticleResponse()
                        .setId(articleId)
                        .setUserId(userId)
                        .setUsername(article.getUser().getUsername())
                        .setTitle(article.getTitle())
                        .setDescription(article.getDescription())
                        .setText(article.getText())
                        .setTags(article.getTags())
                        .setLikes(likeRepository.countByArticle(article.getId()))
                        .setMeLiked(likeRepository.existsByArticleAndUsr(articleId, userId))
                        .setPublished(article.isPublished());
                List<ArticleFile> articleFiles = articleFileRepository.findAllByArticleId(articleId);
                if (articleFiles != null && !articleFiles.isEmpty()) {
                    newResponse
                            .setFilesUrl(articleFiles.stream()
                                    .map(ArticleFile::getUrlTemplate).collect(Collectors.toList()));
                }
                return newResponse;
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

                    List<ArticleFile> articleFiles = articleFileRepository.findAllByArticleId(article.getId());
                    if (articleFiles != null && !articleFiles.isEmpty()) {
                        articleResponse
                                .setFilesUrl(articleFiles.stream()
                                        .map(ArticleFile::getUrlTemplate).collect(Collectors.toList()));
                    }
                    articleResponse
                            .setId(article.getId())
                            .setUserId(article.getUser().getId())
                            .setUsername(article.getUser().getUsername())
                            .setTitle(article.getTitle())
                            .setDescription(article.getDescription())
                            .setText(article.getText())
                            .setTags(article.getTags())
                            .setPublished(article.isPublished());
                    return new ResponseEntity<>(articleResponse, HttpStatus.OK);
                })
                .orElse(
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Transactional
    public ResponseEntity<Long> createArticle(ArticleRequest articleRequest, List<MultipartFile> files) {
        try {

            Article article = articleMapper
                    .fillCreateArticle(articleRequest)
                    .setCreateAt(new Date())
                    .setUser(userRepository.findById(articleRequest.getUserId()).orElseThrow());

            articleRepository.save(article);

            if (!files.isEmpty()) {
                files.forEach(file -> {
                    FireBaseResponseDto fireBaseResponseDto = fileService.upload(file);
                    articleFileRepository.save(new ArticleFile()
                            .setUrlTemplate(fireBaseResponseDto.getUrlTemplate())
                            .setFileName(fireBaseResponseDto.getFileName())
                            .setArticle(article));
                });
            }

            return new ResponseEntity<>(article.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<HttpStatus> updateArticle(long id, ArticleRequest articleRequest) {

        Article updateArticle = articleRepository
                .findById(id)
                .map(a -> articleMapper.fillUpdateArticle(articleRequest, a)
                        .setUpdateAt(new Date()))
                .orElseThrow(() -> new EntityNotFoundException("Can't find Article with id: " + id));
        articleRepository.save(updateArticle);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    public ResponseEntity<HttpStatus> deleteArticle(Long id) {
        try {
            List<ArticleFile> articleFileList = articleFileRepository.findAllByArticleId(id);

            if (!articleFileList.isEmpty()) {
                articleFileList
                        .forEach(articleFile -> {
                            try {
                                fileService.deleteFile(articleFile.getFileName());
                                articleFileRepository.delete(articleFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
            articleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
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

    private boolean isAdminOrModer(LocalUser localUser) {
        Set<String> roleSet = localUser
                .getUser()
                .getRoles()
                .stream().map(Role::getName).collect(Collectors.toSet());
        return roleSet.contains(ERole.ROLE_ADMIN.name()) || roleSet.contains(ERole.ROLE_MODERATOR);

    }
}
