package com.maitriconnect.auth_service.service;

import com.maitriconnect.auth_service.dto.AuthResponse;
import com.maitriconnect.auth_service.dto.LoginRequest;
import com.maitriconnect.auth_service.dto.RegisterRequest;
import com.maitriconnect.auth_service.dto.UpdateProfileRequest;
import com.maitriconnect.auth_service.dto.UserProfileResponse;
import com.maitriconnect.auth_service.model.User;
import com.maitriconnect.auth_service.repository.UserRepository;
import com.maitriconnect.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName()
        );

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Update user status to online
        user.setOnline(true);
        user.setLastLogin(LocalDateTime.now());
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get all users (for displaying in chat)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    // Search users by username or name
    public List<UserProfileResponse> searchUsers(String searchTerm) {
        return userRepository.findByUsernameOrNameContaining(searchTerm).stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    // Get online users
    public List<UserProfileResponse> getOnlineUsers() {
        return userRepository.findByOnlineTrue().stream()
                .map(this::convertToUserProfileResponse)
                .collect(Collectors.toList());
    }

    // Update user profile
    public UserProfileResponse updateUserProfile(String userId, UpdateProfileRequest request) {
        User user = getUserById(userId);
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        
        userRepository.save(user);
        return convertToUserProfileResponse(user);
    }

    // Update online status
    public void updateOnlineStatus(String userId, boolean online) {
        User user = getUserById(userId);
        user.setOnline(online);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    // Convert User to UserProfileResponse
    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setProfilePicture(user.getProfilePicture());
        response.setStatus(user.getStatus());
        response.setOnline(user.isOnline());
        
        // Format last seen
        if (user.getLastSeen() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.setLastSeen(user.getLastSeen().format(formatter));
        }
        
        return response;
    }
}