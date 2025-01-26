package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.auth.AuthenticationDTO;
import com.cosmo.wanda_web.dto.auth.RegisterDTO;
import com.cosmo.wanda_web.entities.Role;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.RoleRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public void login(AuthenticationDTO dto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        Authentication auth = authenticationManager.authenticate(usernamePassword);
    }

    @Transactional
    public void register(RegisterDTO dto) {
        User result = userRepository.findByEmail(dto.getEmail().toLowerCase());
        if (result != null){
            throw new RuntimeException("Esse email já está sendo usado");
        }
        User newUser = new User();
        String passwordEncode = passwordEncoder.encode(dto.getPassword());
        newUser.setEmail(dto.getEmail());
        newUser.setName(dto.getName());
        newUser.setPassword(passwordEncode);
        Role roleUser = roleRepository.getReferenceById(1L);
        newUser.addRole(roleUser);
        userRepository.save(newUser);
    }
}
