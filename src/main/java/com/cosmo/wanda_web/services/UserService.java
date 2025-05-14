package com.cosmo.wanda_web.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.dto.auth.AccessTokenDTO;
import com.cosmo.wanda_web.dto.auth.AuthenticationDTO;
import com.cosmo.wanda_web.dto.auth.RegisterDTO;
import com.cosmo.wanda_web.entities.Player;
import com.cosmo.wanda_web.entities.Role;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.TokenService;
import com.cosmo.wanda_web.repositories.PlayerRepository;
import com.cosmo.wanda_web.repositories.RoleRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.RegisterException;
import com.cosmo.wanda_web.services.utils.PlayerWithCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerWithCharacter playerWithCharacter;

    @Autowired
    private TokenService tokenService;

    @Transactional(readOnly = true)
    public AccessTokenDTO login(AuthenticationDTO dto) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(dto.getEmail().toLowerCase().trim(), dto.getPassword());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((User) auth.getPrincipal());
        return new AccessTokenDTO(token);
    }

    @Transactional
    public void register(RegisterDTO dto) {
        User result = userRepository.findByEmail(dto.getEmail().toLowerCase().trim());
        if (result != null) {
            throw new RegisterException("Esse email já está sendo usado");
        }
        User newUser = new User();
        String passwordEncode = passwordEncoder.encode(dto.getPassword());
        newUser.setEmail(dto.getEmail().toLowerCase().trim());
        newUser.setName(dto.getName());
        newUser.setPassword(passwordEncode);
        Role roleUser = roleRepository.getReferenceById(1L);
        newUser.addRole(roleUser);
        newUser = userRepository.save(newUser);

        Player player = playerWithCharacter.createPlayer(newUser);

        playerRepository.save(player);
    }

    @Transactional
    public UserDTO getMe() {
        User user = authenticated();
        return new UserDTO(user);
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String token = authentication.getCredentials().toString(); // Aqui pega o token que setei lá no filter no conntexto

            DecodedJWT decodedJWT = JWT.decode(token);

            String username = decodedJWT.getClaim("username").asString();

            User user = userRepository.findByEmail(username.toLowerCase());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Algum outro erro");
        }
    }
}
