package com.malibu.app.service.user;


import com.malibu.app.dto.LocalUser;
import com.malibu.app.exception.ResourceNotFoundException;
import com.malibu.app.model.User;
import com.malibu.app.util.GeneralUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("localUserDetailService")
@AllArgsConstructor
public class LocalUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    @Transactional
    public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " was not found in the database");
        }
        return createLocalUser(user);
    }

    @Transactional
    public LocalUser loadUserById(Long id) {
        User user = userService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return createLocalUser(user);
    }

    private LocalUser createLocalUser(User user) {
        return new LocalUser(user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                GeneralUtils.buildSimpleGrantedAuthorities(user.getRoles()), user);
    }
}
