package org.example.thuan_security.service.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.config.security.JwtTokenProvider;
import org.example.thuan_security.controller.FileUploadController;
import org.example.thuan_security.model.Roles;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.RoleRepository;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.repository.UserRepositoryCustom;
import org.example.thuan_security.request.*;
import org.example.thuan_security.response.*;
import org.example.thuan_security.service.*;
import org.example.thuan_security.service.keycloak.UserKCLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserKCLService userKCLService;
    @Autowired
    private UserRepositoryCustom userRepositoryCustom;
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    private final Map<String, String> tokenStorage = new HashMap<>();
    private final Cache<String, String> otpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Autowired
    private FileUploadController fileUploadController;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Users user = userRepository.findByEmail(loginRequest.getEmail());
            if (user == null) {
                return new LoginResponse("404", "User not found", "", null, null);
            }

            if (!user.isVerified()) {
                return new LoginResponse("403", "Account not verified. Please verify your account.", "", null, null);
            }

            if (!user.isUserEnabled()) {
                return new LoginResponse("403", "Account not enabled.", "", null, null);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                log.info("User {} logged in successfully. Sending OTP...", email);


                String otp = generateOTP();
                otpCache.put(email, otp);
                otpCache.asMap().forEach((key, value) -> {
                    System.out.println("Key: " + key + ", Value: " + value);
                });
                emailService.sendMail(email, "Your OTP Code", "Your OTP code is: " + otp);

                return new LoginResponse("202", "OTP sent to email. Please verify.", null, null, null);
            }
        } catch (Exception e) {
            return new LoginResponse("401", "Wrong email or password", "", null, null);
        }

        return new LoginResponse("401", "Wrong email or password", "", null, null);
    }

    @Override
    public LoginResponse validateLoginWithOtp(String email, String otp) {
        if (validateOtp(email, otp)) {
            deleteOtp(email);
            Users user = userRepository.findByEmail(email);
            if (user != null) {
                List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                String token = jwtTokenProvider.createToken(
                        new UsernamePasswordAuthenticationToken(email, null, authorities),
                        email);

                String rftoken = refreshTokenService.createRefreshToken(email);

                LocalDateTime expiration = jwtTokenProvider.extractExpiration(token);

                return new LoginResponse("200", "Login successful", token, rftoken, expiration);
            } else {
                return new LoginResponse("404", "User not found", "", null, null);
            }
        } else {
            return new LoginResponse("401", "Invalid or expired OTP", "", null, null);
        }
    }


    @Override
    public UserKCLResponse register(RegisterRequest registerRequest) {
//        if (isPasswordValid(registerRequest)) {
//            ApiResponse1<String> apiResponse = new ApiResponse1<>();
//            apiResponse.setMessage("Password and confirm password do not match");
//            apiResponse.setResult("");
//            return apiResponse;
//        }

        if (isEmailValid(registerRequest)) {


            Users newUser = new Users();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            Roles roles = roleRepository.findByName("ROLE_USER");
            String roleName = roles.getName();
            newUser.setRoles(Collections.singleton(roleName));
            String fullname = registerRequest.getFirstName() + " " + registerRequest.getLastName();
            newUser.setFullName(fullname);
            newUser.setDob(registerRequest.getDob());
            userRepository.save(newUser);
            String verificationLink = "http://localhost:8081/api/auth/verifiedAccount?email=" + registerRequest.getEmail();
            emailService.sendMail(
                    registerRequest.getEmail(),
                    "Xác nhận đăng ký",
                    "<p>Chào " + fullname + ",</p>"
                            + "<p>Vui lòng nhấn vào liên kết bên dưới để xác nhận đăng ký tài khoản:</p>"
                            + "<a href=\"" + verificationLink + "\">Xác nhận đăng ký</a>"
            );

            RegisterResponseDTO responseDTO = new RegisterResponseDTO(
                    newUser.getEmail(),
                    newUser.getFullName(),
                    ""
            );
            UserKCLResponse apiResponse = new UserKCLResponse();
            apiResponse.setUsername(registerRequest.getUsername());
            return apiResponse;
        } else {
            UserKCLResponse apiResponse = new UserKCLResponse();
            apiResponse.setUsername(registerRequest.getUsername());


            return apiResponse;
        }
    }

    @Override
    public UserResponse getUserInfo(String token) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
            Jwt jwt = (Jwt) principal;
            log.info("Keycloak JWT: {}", jwt.toString());
            String email = jwt.getClaimAsString("email");
            log.info("Email: {}", email);
            Users user = userRepository.findByEmail(email);

            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(email);
            userResponse.setLastName(user.getFullName());
            userResponse.setImgUrl(user.getImage_url());
            return userResponse;


    }

    @Override
    public ApiResponse updateUserInfo(String token, UserResponse userResponse) {
        if (isKeycloakEnabled) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            // Kiểm tra xem principal có phải là Jwt không
            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                log.info("JWT Token: " + jwt.toString());

                String email = jwt.getClaimAsString("email");
                if (email == null || email.isEmpty()) {
                    return new ApiResponse<>(400, "Email not found in JWT", 0, null);
                }
                log.info("User email: " + email);

                // Cập nhật thông tin người dùng
                Users users = userRepository.findByEmail(email);
                if (users != null) {
                    users.setFullName(userResponse.getFullName());
                    userRepository.save(users);
                    return new ApiResponse<>(200, "Updated", 1, null);
                } else {
                    return new ApiResponse<>(404, "User not found", 0, null);
                }
            }
        } else {
            // Xử lý với token ngoài Keycloak
            String email = jwtTokenProvider.extractClaims(token);
            if (email == null || email.isEmpty()) {
                return new ApiResponse<>(400, "Invalid token or email not found", 0, null);
            }

            try {
                Users users = userRepository.findByEmail(email);
                if (users != null) {
                    users.setFullName(userResponse.getFullName());
                    userRepository.save(users);
                    return new ApiResponse<>(200, "Updated", 1, null);
                } else {
                    return new ApiResponse<>(404, "User not found", 0, null);
                }
            } catch (Exception e) {
                log.error("Error updating user: ", e);
                return new ApiResponse<>(500, "Internal server error", 0, null);
            }
        }

        return new ApiResponse<>(400, "Unknown error", 0, null);
    }


    @Override
    public boolean isVerifiedAccount(String email) {
        Users user = userRepository.findByEmail(email);
        user.setVerified(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public ApiResponse changePassword(ChangePasswordRequest changePasswordRequest, String email) {
        Users user = userRepository.findByEmail(email);
        if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            emailService.sendMail(email, "Mật khẩu thay đổi", "Mật khẩu của bạn đã được thay đổi ");
            ApiResponse<String> apiResponse = new ApiResponse<>();
            apiResponse.setCode(1);
            apiResponse.setMessage("Password changed successfully");
            apiResponse.setStatus(200);
            apiResponse.setData(List.of(""));
            return apiResponse;
        } else {
            return new ApiResponse<>(401, "Wrong current password", 0, List.of(""));
        }

    }

    @Override
    public ApiResponse sendMailForgotPassword(String email) {
        Users user = userRepository.findByEmail(email);

        if (user != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String token = jwtTokenProvider.createToken(authentication, email);

            tokenStorage.put(email, token);


            String subject = "Reset Your Password";
            String resetLink = "http://localhost:8080/api/users/reset-password?token=" + token;
            String message = "Dear " + user.getFullName() + ",\n\n" +
                    "We received a request to reset your password. Please click the link below to reset it:\n" +
                    resetLink + "\n\n"
                    +
                    "This link will expire in 30 minutes.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\nYour Team";


            emailService.sendMail(email, subject, message);

            log.info("Reset password link sent to email: {}", email);

            return new ApiResponse<>(200, "Link to reset your password has been sent to your email.", 1, List.of(""));
        }

        return new ApiResponse<>(401, "Cannot find user with this email", 0, List.of(""));
    }


    @Override
    public ApiResponse changeForgotPassword(String email, String token, String newPassword) {
        if (isValidOTP(email, token)) {
            Users user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            emailService.sendMail(email, "Mật khẩu thay đổi", "Mật khẩu của bạn đã được thay đổi ");

            return new ApiResponse<>(200, "Password changed successfully", 1, List.of(""));

        }
        return new ApiResponse<>(401, "Wrong OTP", 0, List.of(""));
    }

    @Override
    public String createUser(RegisterRequest registerRequest) {
        Users newUser = new Users();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Roles roles = roleRepository.findByName("ROLE_USER");
        String roleName = roles.getName();
        newUser.setRoles(Collections.singleton(roleName));
        String fullname = registerRequest.getFirstName() + " " + registerRequest.getLastName();
        newUser.setFullName(fullname);
        newUser.setDob(registerRequest.getDob());
        newUser.setVerified(true);
        userRepository.save(newUser);

        return "Created user successfull";
    }

    @Override
    public String updateEnabled(String id, RegisterRequest request) {
        Users user = userRepository.findByUserId(id);
        if (user != null) {
            user.setUserEnabled(request.isEnabled());
            userRepository.save(user);
            userKCLService.updateEnabled(id,request);
            return "Updated user successfully";
        } else return "User does not exist";
    }

    @Override
    public String resetPassword(String userId, ResetPasswordRequest request) {
        Users users=userRepository.findById(Long.valueOf(userId)).orElseThrow();
        if (users != null) {
            users.setPassword(passwordEncoder.encode(request.getValue()));
            userRepository.save(users);
            userKCLService.resetPassword(userId,request);
            return "Reset successful";
        }
        else return "User does not exist";
    }

    @Override
    public Page<UserResponse> getAllUsers(SearchRequest searchRequest) {

        Pageable sortedPageable = createPageable(searchRequest);

        Page<Users> usersPage = userRepository.findAll(sortedPageable);
        if (usersPage.isEmpty()) {
            System.out.println("No users found.");
        }

        return convertToUserResponse(usersPage);
    }


    @Override
    public String deleteUser(Long userId) {
        Users users=userRepository.findById(userId).orElseThrow();
        log.info(users.getFullName());
        if(users!=null && !users.isDeleted()) {
            users.setDeleted(true);
            userRepository.save(users);
            return "Deleted ok";
        }
        else return "User already deleted";
    }

    @Override
    public Page<UserResponse> searchUsers(UserSearchRequest request) {
        // Lấy danh sách người dùng từ userRepository
        Page<Users> usersPage = userRepository.search(request);

        // Chuyển đổi từ List<Users> sang List<UserResponse>
        List<UserResponse> userResponses = usersPage.getContent().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRoles(),
                        user.getImage_url()))
                .collect(Collectors.toList());

        // Tạo Page<UserResponse> từ List<UserResponse>
        return new PageImpl<>(userResponses, PageRequest.of(request.getPage(), request.getSize()), usersPage.getTotalElements());
    }



    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String generateRefreshToken(String token) {
        return UUID.randomUUID().toString();
    }

    public boolean validateOtp(String key, String inputOtp) {
        String cachedOtp = otpCache.getIfPresent(key);
        return cachedOtp != null && cachedOtp.equals(inputOtp);
    }

    // Xóa OTP sau khi dùng hoặc không còn hiệu lực
    public void deleteOtp(String key) {
        otpCache.invalidate(key);
    }


    public boolean isValidOTP(String email, String otp) {
        if (tokenStorage.containsKey(email) && tokenStorage.get(email).equals(otp)) {
            tokenStorage.remove(email);
            return true;
        }
        return false;
    }


    public boolean isEmailValid(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()) == null) {
            return true;
        }
        return false;
    }

    public Page<UserResponse> convertToUserResponse(Page<Users> usersPage) {
        return usersPage.map(user -> new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRoles(),
                user.getImage_url()
        ));
    }
    public static Pageable createPageable(SearchRequest searchRequest) {
        if ("desc".equalsIgnoreCase(searchRequest.getSortDirection())) {
            return PageRequest.of(
                    searchRequest.getPage(),
                    searchRequest.getSize(),
                    Sort.by(Sort.Order.desc(searchRequest.getKeyword()))
            );
        }
        return PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.by(Sort.Order.asc(searchRequest.getKeyword()))
        );
    }

}
