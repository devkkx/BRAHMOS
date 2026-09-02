package com.brahmosbhawan.security;

import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Support login by either email or studentId
        User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByStudentId(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or studentId: " + username)));

        return new UserPrincipal(user);
    }
}
