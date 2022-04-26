package com.malibu.springmalibuapp.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageResponseDto {
    private String message;
}
