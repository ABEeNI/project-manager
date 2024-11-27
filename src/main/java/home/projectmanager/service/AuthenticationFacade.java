package home.projectmanager.service;

import home.projectmanager.entity.User;
import home.projectmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String currentPrincipalName = getCurrentPrincipalName();
        return userRepository.findByEmail(currentPrincipalName)
               .orElseThrow(() -> new UsernameNotFoundException("User with email " + currentPrincipalName + " not found"));
    }

    private static String getCurrentPrincipalName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
