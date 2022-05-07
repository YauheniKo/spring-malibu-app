package com.malibu.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class ArticleRequest {
    private Long userId;
    private String title;
    private String description;
    private String text;
    private String tagName;
    private boolean published;
}
