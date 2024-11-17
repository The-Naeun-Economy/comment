package com.repick.comment.controller;

import com.repick.comment.domain.Comment;
import com.repick.comment.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @GetMapping
    public List<Comment> getComments() {
        return service.getAllComments();
    }

    @GetMapping("/{id}")
    public Comment getComment(@PathVariable Long id) {
        return service.getCommentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody CreateCommentRequest request) {
        return service.createComment(request);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@RequestBody UpdateCommentRequest request) {
        return service.updateComment(request);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
    }
}
