package com.malibu.springmalibuapp.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Accessors(chain = true)
public class LoginRequestDto {
	@NotBlank
  private String username;

	@NotBlank
	private String password;

}
