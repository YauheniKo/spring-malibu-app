package com.malibu.springmalibuapp.payload.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageResponse {
    private String message;
}
