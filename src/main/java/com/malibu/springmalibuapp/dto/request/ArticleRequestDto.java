package com.malibu.springmalibuapp.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class ArticleRequestDto {
    private Long userId;
    private String title;
    private String description;
    private String text;
    private Set<String> tagName;
}
