package com.malibu.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FireBaseResponseDto {
    String fileName;
    String urlTemplate;
}
