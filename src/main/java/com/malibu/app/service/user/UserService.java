package com.malibu.app.service.user;

import com.malibu.app.dto.LocalUser;
import com.malibu.app.dto.SignUpRequest;
import com.malibu.app.exception.UserAlreadyExistAuthenticationException;
import com.malibu.app.model.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    public User registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;

    User findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    List<User> findAllUser();

    LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes,
                                      OidcIdToken idToken, OidcUserInfo userInfo);


}

