package com.malibu.app.config;

import com.malibu.app.dto.SocialProvider;
import com.malibu.app.model.ERole;
import com.malibu.app.model.Role;
import com.malibu.app.model.User;
import com.malibu.app.repository.RoleRepository;
import com.malibu.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // Create initial roles
        Role userRole = createRoleIfNotFound(ERole.ROLE_USER.name());
        Role adminRole = createRoleIfNotFound(ERole.ROLE_ADMIN.name());
        Role modRole = createRoleIfNotFound(ERole.ROLE_MODERATOR.name());
        createUserIfNotFound("riemi@bk.ru", Set.of(userRole, adminRole, modRole));
        alreadySetup = true;
    }

    @Transactional
    User createUserIfNotFound(final String email, Set<Role> roles) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setUsername("Admin");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("riemi@b"));
            user.setRoles(roles);
            user.setProvider(SocialProvider.LOCAL.getProviderType());
            user.setEnabled(true);
            Date now = Calendar.getInstance().getTime();
            user.setCreatedDate(now);
            user.setModifiedDate(now);
            user = userRepository.save(user);
        }
        return user;
    }

    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.save(new Role(name));
        }
        return role;
    }
}
