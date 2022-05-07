package com.malibu.app.dto;

import com.malibu.app.entity.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class ArticleResponse {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private String text;
    private Set<Tag> tag;
    private Long likes;
    private Boolean meLiked;
    private boolean published;

}
