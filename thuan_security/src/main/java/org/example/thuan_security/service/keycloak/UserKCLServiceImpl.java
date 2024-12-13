package org.example.thuan_security.service.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.Roles;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.RoleRepository;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.request.*;
import org.example.thuan_security.response.UserKCLResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserKCLServiceImpl implements UserKCLService {

    private final UserRepository userRepository;
    private final IdentityClient identityClient;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${idp.client-id}")
    @NonFinal
    private String clientId;
    @Value("${idp.client-secret}")
    @NonFinal
    private String secretId;

    @Override
    public UserKCLResponse register(RegisterRequest request) {
        // create account keycloak

        // exchange client token
        var token = identityClient.exchangeToken(TokenExchangeParam.builder().grant_type("client_credentials").client_id(clientId).client_secret(secretId).scope("openid").build());
        log.info("token: {}", token);

        var creationResponse = identityClient.createUser("Bearer " + token.getAccessToken(), UserCreationParam.builder().username(request.getUsername()).firstName(request.getFirstName()).lastName(request.getLastName()).email(request.getEmail()).enabled(true).emailVerified(false).credentials(List.of(Credential.builder().type("password").temporary(false).value(request.getPassword()).build())).build());

        String userId = extractUserId(creationResponse);
        log.info("UserId {}", userId);
        var users = toUsers(request);
        users.setUserId(userId);

        Roles roles = roleRepository.findByName("ROLE_USER");
        String roleName = roles.getName();
        users.setRoles(Collections.singleton(roleName));
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users = userRepository.save(users);
        log.info(toUserKCLResponse(users).toString());
        return toUserKCLResponse(users);

    }

    @Override
    public String updateEnabled(String id, RegisterRequest request) {
        try {
            if (userRepository.existsByUserId(id)) {


                var token = identityClient.exchangeToken(TokenExchangeParam.builder().grant_type("client_credentials").client_id(clientId).client_secret(secretId).scope("openid").build());

                RegisterRequest updatedRequest = new RegisterRequest(request.isEnabled());

                log.info("updatedRequest: {}", updatedRequest);
                var updateResponse = identityClient.updateUser("Bearer " + token.getAccessToken(), id, updatedRequest);

                Users user = userRepository.findByUserId(id);

                user.setUserEnabled(request.isEnabled());


                userRepository.save(user);

                return "Updated";
            } else {
                return "User not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while updating user";
        }
    }

    @Override
    public String resetPassword(String id, ResetPasswordRequest request) {
        try {
            if (userRepository.existsById(Long.valueOf(id))) {
                var token = identityClient.exchangeToken(TokenExchangeParam.builder().grant_type("client_credentials").client_id(clientId).client_secret(secretId).scope("openid").build());
                ResetPasswordRequest updatedRequest = new ResetPasswordRequest(request.getValue());
                Users users=userRepository.findById(Long.valueOf(id)).orElseThrow();
                String userId= users.getUserId();
                log.info(userId);
                var updateResponse = identityClient.resetPassword("Bearer " + token.getAccessToken(), userId, updatedRequest);
                Users user = userRepository.findByUserId(userId);
                user.setPassword(passwordEncoder.encode(request.getValue()));
                userRepository.save(user);
                return "Reset successful";
            } else {
                return "Reset failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while updating user";

        }
    }


    private String extractUserId(ResponseEntity<?> response) {
        if (response != null && response.getHeaders() != null && response.getHeaders().containsKey("Location")) {
            String location = response.getHeaders().getFirst("Location");

            if (location != null && !location.isEmpty()) {
                String[] splitedStr = location.split("/");

                if (splitedStr.length > 0) {
                    return splitedStr[splitedStr.length - 1];
                }
            }
        }

        return null;
    }

    public Users toUsers(RegisterRequest requestKCL) {
        Users users = new Users();
        users.setFullName(requestKCL.getFirstName() + requestKCL.getLastName());
        users.setEmail(requestKCL.getEmail());
        users.setPassword(requestKCL.getPassword());
        users.setDob(requestKCL.getDob());
        return users;
    }

    public UserKCLResponse toUserKCLResponse(Users users) {
        UserKCLResponse response = new UserKCLResponse();
        response.setUserId(users.getUserId());
        response.setFirstName(users.getFullName());
        response.setEmail(users.getEmail());
        response.setDob(users.getDob());
        response.setUsername(users.getUsername());
        response.setUserId(users.getUserId());
        return response;
    }
}
