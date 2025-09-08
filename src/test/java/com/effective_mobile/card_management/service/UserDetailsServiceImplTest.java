package com.effective_mobile.card_management.service;

import com.effective_mobile.card_management.entity.User;
import com.effective_mobile.card_management.enums.Role;
import com.effective_mobile.card_management.repository.UserRepository;
import com.effective_mobile.card_management.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void loadUserByUsername_UserFound() {

        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());

        GrantedAuthority authority = userDetails.getAuthorities().stream().findFirst().orElse(null);
        assertNotNull(authority);
        assertEquals("ROLE_USER", authority.getAuthority());

        verify(userRepository, times(1)).findByUsername(username);
    }


    @Test
    void loadUserByUsername_UserNotFound() {
        // Подготовка
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }
}
