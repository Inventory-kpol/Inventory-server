package kpol.Inventory.domain.member.controller;

import kpol.Inventory.domain.board.dto.res.BoardResponseDto;
import kpol.Inventory.domain.member.dto.req.MemberRequestDto;
import kpol.Inventory.domain.member.dto.res.MemberResponseDto;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.domain.member.service.MypageService;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/members/me")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    // 유저 정보 조회
    @GetMapping
    public ResponseEntity<MemberResponseDto> getMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        MemberResponseDto memberResponse = mypageService.getMemberById(member.getId());
        return ResponseEntity.ok(memberResponse);
     }

    // 유저 정보 수정
    @PutMapping
    public ResponseEntity<MemberResponseDto> updateMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MemberRequestDto updateDto) {
        Member member = userDetails.getMember();
        MemberResponseDto updatedMember = mypageService.updateMember(
                member.getId(), updateDto.getNickname(), updateDto.getPassword());

        return ResponseEntity.ok(updatedMember);
    }

    // 작성 게시물 조회
    @GetMapping("/boards")
    public ResponseEntity<List<BoardResponseDto>> getMyBoards(
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        List<BoardResponseDto> myBoards = mypageService.getMyBoards(member.getId());
        return ResponseEntity.ok(myBoards);
     }

    // 좋아요 게시물 조회
    @GetMapping("/liked-boards")
    public ResponseEntity<List<BoardResponseDto>> getLikedBoards(
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        List<BoardResponseDto> likedBoards = mypageService.getLikedBoards(member.getId());
        return ResponseEntity.ok(likedBoards);

    }
}