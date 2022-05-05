package com.malibu.app.payload.response;

import com.malibu.app.entity.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
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
    private boolean published;

}
