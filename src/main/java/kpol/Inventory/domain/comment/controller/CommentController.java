package kpol.Inventory.domain.comment.controller;


import jakarta.validation.Valid;
import kpol.Inventory.domain.comment.dto.req.CommentRequestDto;
import kpol.Inventory.domain.comment.dto.res.CommentResponseDto;
import kpol.Inventory.domain.comment.service.CommentService;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성 API
    @PostMapping("/{boardId}")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long boardId, // URL 경로에서 boardId 추출
            @AuthenticationPrincipal UserDetailsImpl userDetails, // 현재 로그인한 사용자 정보
            @Valid @RequestBody CommentRequestDto requestDto) { // 요청 본문 데이터
        return ResponseEntity.status(HttpStatus.OK).body(commentService.createComment(boardId, userDetails.getMember(), requestDto));
    }

    //boardId에 속한 댓글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentByBoard(boardId));
    }

    //수정 API
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails, // 현재 로그인한 사용자 정보
            @Valid @RequestBody CommentRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(commentId,requestDto));
    }

    // 삭제 API
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId, userDetails.getMember()));
    }
}
