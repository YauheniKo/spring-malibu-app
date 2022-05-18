package com.malibu.app.dto;

import com.malibu.app.entity.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Accessors(chain = true)
public class ArticleRequest {
    private Long userId;
    private String title;
    private String description;
    private String text;
    private Set<Tag> tags;
    private boolean published;
}
