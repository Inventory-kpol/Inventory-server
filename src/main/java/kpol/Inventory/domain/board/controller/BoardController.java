package kpol.Inventory.domain.board.controller;

import jakarta.validation.Valid;
import kpol.Inventory.domain.board.dto.req.BoardRequestDto;
import kpol.Inventory.domain.board.dto.req.BoardUpdateRequestDto;
import kpol.Inventory.domain.board.dto.res.BoardDetailResponseDto;
import kpol.Inventory.domain.board.dto.res.BoardListResponseDto;
import kpol.Inventory.domain.board.dto.res.BoardPageResponseDto;
import kpol.Inventory.domain.board.service.BoardService;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping
    public ResponseEntity<BoardDetailResponseDto> createBoard(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @Valid @RequestBody BoardRequestDto boardRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.createBoard(userDetails, boardRequestDto));
    }

    // 글 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<BoardPageResponseDto> searchBoard(@RequestParam int page, @RequestParam String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.searchBoard(page, keyword));
    }

    // 해시태그 검색
    @GetMapping("/search/tag")
    public ResponseEntity<BoardPageResponseDto> searchBoardByTag(@RequestParam int page, @RequestParam String boardTag) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.searchBoardByTag(page, boardTag));
    }

    // 글 내용 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponseDto> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.getBoard(boardId));
    }

    // 글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponseDto> updateBoard(@PathVariable Long boardId,
                                                              @Valid @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.updateBoard(boardId ,boardUpdateRequestDto));
    }

    // 글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Boolean> deleteBoard(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.deleteBoard(boardId));
    }

    // 최신 글 3개 조회
    @GetMapping("/new")
    public ResponseEntity<List<BoardListResponseDto>> getLatestPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.getLatestPosts());
    }

    // 글 좋아요
    @PostMapping("/like/{boardId}")
    public ResponseEntity<BoardDetailResponseDto> likeBoard(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.likeBoard(userDetails, boardId));
    }
}
