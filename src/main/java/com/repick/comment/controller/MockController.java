//package com.repick.comment.controller;
//
//import com.repick.comment.dto.UserResponse;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class MockController {
//
//    @GetMapping("/users/{userId}")
//    public UserResponse mockUser(@PathVariable Long userId) {
//        // 항상 유효한 사용자로 간주
//        return new UserResponse(userId, "MockUser");
//    }
//
//    @GetMapping("/posts/{postId}/exists")
//    public boolean mockPost(@PathVariable Long postId) {
//        // 항상 게시글이 존재한다고 간주
//        return true;
//    }
//}
