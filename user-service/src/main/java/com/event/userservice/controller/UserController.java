package com.event.userservice.controller;


import com.event.userservice.dto.*;
import com.event.userservice.dto.LoginRequest;
import com.event.userservice.exceptions.GenericBadRequestException;
import com.event.userservice.exceptions.ProfileNotFoundException;
import com.event.userservice.model.Profile;
import com.event.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;


    @PutMapping("/follow/{followeeUsername}/{followerUsername}")
    public void followUser(@PathVariable String followeeUsername, @PathVariable String followerUsername){
        userService.followUser(followeeUsername, followerUsername);
    }

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody ApplicationUserRequest userInfo){
        userService.addUser(userInfo);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) throws GenericBadRequestException {
        return userService.login(loginRequest);
    }

    @GetMapping("/follower-list/{username}")
    public FollowerResponse getFollowerList(@PathVariable String username){
        return userService.getFollowers(username);
    }

    @GetMapping("/following-list/{username}")
    public FollowingResponse getFollowingList(@PathVariable String username){
        return userService.getFollowees(username);
    }


    @PostMapping("/profile/{username}")
    public AddProfileInfoResponse addProfileInfo(@Valid @RequestBody ProfileRequest profileRequest, @PathVariable String username){
        userService.addProfileInfo(modelMapper.map(profileRequest, Profile.class), username);
        return AddProfileInfoResponse.builder()
                .isSuccess(true)
                .addImgUrl("api/v1/users/profile-img/" + username)
                .build();
    }

    @PostMapping("/profile-img/{username}")
    public void addProfilePhoto(@PathVariable String username, @RequestParam("image") MultipartFile file) throws IOException{
        userService.addProfilePhoto(file, username);
    }

    @GetMapping("/profile/{username}")
    public ProfileResponse getProfileInfo(@PathVariable String username) throws GenericBadRequestException, ProfileNotFoundException {
        var profileResponse = modelMapper.map(userService.getProfileInfo(username),ProfileResponse.class);
        profileResponse.setImgUrl("/api/v1/users/profile-img/" + username);
        return profileResponse;
    }

    @GetMapping("/profile-img/{username}")
    public ResponseEntity<?> getProfileImg(@PathVariable String username) throws IOException {
        byte[] imgData = userService.getProfileImg(username);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imgData);
    }

    @GetMapping("/profile-check/{username}")
    public ProfileCheckResponse checkIsProfilePresent(@PathVariable String username) throws GenericBadRequestException {
        return ProfileCheckResponse.builder()
                .isProfileExists(userService.checkIfProfileExists(username))
                .build();
    }


}
