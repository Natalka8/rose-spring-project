package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User account has been deleted: " + username);
        }

        if (!isUserActive(user)) {
            throw new UsernameNotFoundException("User account is not active: " + username);
        }

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("User account has been deleted: " + email);
        }

        if (!isUserActive(user)) {
            throw new UsernameNotFoundException("User account is not active: " + email);
        }

        return new CustomUserDetails(user);
    }

    private boolean isUserActive(User user) {
        boolean isEnabled = user.isEnabled();
        boolean isAccountNonLocked = user.isAccountNonLocked();
        boolean notDeleted = user.getDeletedAt() == null;

        // FIXED: using internal enum User.UserStatus
        User.UserStatus status = user.getStatus();
        boolean isActiveStatus = status == User.UserStatus.ACTIVE || status == User.UserStatus.PENDING;

        return isEnabled && isAccountNonLocked && notDeleted && isActiveStatus;
    }
}