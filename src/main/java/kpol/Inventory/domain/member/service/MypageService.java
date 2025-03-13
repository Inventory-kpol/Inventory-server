package kpol.Inventory.domain.member.service;

import kpol.Inventory.domain.board.dto.res.BoardResponseDto;
import kpol.Inventory.domain.member.dto.res.MemberResponseDto;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;

    // 인증된 유저 정보 조회
    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("인증된 사용자가 없습니다."));

        return new MemberResponseDto(member.getId(), member.getNickname(), member.getNickname(), member.getEmail());
    }

    // 유저 정보 수정
    public MemberResponseDto updateMember(Long id, String nickname, String password) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자가 없습니다"));

        // 닉네임, 비밀번호 변경
        member.updateInfo(nickname, password);
        memberRepository.save(member);

        return new MemberResponseDto(member.getId(), member.getNickname(), member.getNickname(), member.getEmail());
    }

    // 작성 게시물 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getMyBoards(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return member.getBoards().stream()
                .map(board -> new BoardResponseDto(
                        board.getTitle(),  // 글의 제목
                        board.getContent(), // 글의 내용
                        board.getCreatedAt(), // 작성 일시
                        board.getLikeCount() // 글 좋아요 수
                ))
                .collect(Collectors.toList());
    }
    // 좋아요 게시물 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getLikedBoards(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return member.getLikeBoard().stream()
                .map(board -> new BoardResponseDto(
                        board.getTitle(),  // 글의 제목
                        board.getContent(), // 글의 내용
                        board.getCreatedAt(), // 작성 일시
                        board.getLikeCount() // 글 좋아요 수
                ))
                .collect(Collectors.toList());
    }

}


