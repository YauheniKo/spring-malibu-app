package com.malibu.app.mappers;

import com.malibu.app.dto.ArticleRequest;
import com.malibu.app.entity.Article;
import com.malibu.app.entity.Tag;
import com.malibu.app.repository.TagRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {
    private final TagRepository tagRepository;

    public ArticleMapper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Article fillCreateArticle(ArticleRequest articleRequest) {
        return fillArticle(articleRequest, new Article())
                .setTags(getTags(articleRequest.getTags()));
    }

    public Article fillUpdateArticle(ArticleRequest articleRequest, Article currArticle) {
        Article article = fillArticle(articleRequest, currArticle);
        return article.setTags(getTags(articleRequest.getTags()));

    }

    private Article fillArticle(ArticleRequest articleRequest, Article article) {
        return article
                .setTitle(articleRequest.getTitle())
                .setDescription(articleRequest.getDescription())
                .setText(articleRequest.getText())
                .setPublished(articleRequest.isPublished());
    }

    private Set<Tag> getTags(Set<Tag> tagList) {
        Set<Tag> setCurrentTag = tagList
                .stream()
                .map(res -> tagRepository.findTagByName(res.getName().toLowerCase()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Tag> newSetTag = tagList
                .stream()
                .filter(newTag -> setCurrentTag
                        .stream()
                        .noneMatch(currTg ->
                                Objects.equals(currTg.getName().toLowerCase(), newTag.getName().toLowerCase())))
                .collect(Collectors.toSet());
        Set<Tag> res = new HashSet<>();
        if (!setCurrentTag.isEmpty()) {
            res.addAll(setCurrentTag);
        }
        if (!newSetTag.isEmpty()) {
            res.addAll(tagRepository.saveAll(newSetTag));
        }
        return res;

    }

}


