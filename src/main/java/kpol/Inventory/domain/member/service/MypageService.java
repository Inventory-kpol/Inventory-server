package kpol.Inventory.domain.member.service;

import kpol.Inventory.domain.board.dto.res.BoardResponseDto;
import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.member.dto.res.MemberResponseDto;
import kpol.Inventory.domain.member.entity.LikeBoard;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        List <BoardResponseDto> boardResponseList = new ArrayList<>();

        for (var board : member.getBoards()) {
            BoardResponseDto dto = new BoardResponseDto(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCreatedAt(),
                    board.getLikeCount()
            );
            boardResponseList.add(dto);
        }
        return boardResponseList;
    }

    // 좋아요 게시물 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getLikedBoards(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        List<BoardResponseDto> boardResponseList = new ArrayList<>();

        for (LikeBoard likeBoard : member.getLikeBoard()) {
            Board board = likeBoard.getBoard();

            BoardResponseDto dto = new BoardResponseDto(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCreatedAt(),
                    board.getLikeCount()
            );
            boardResponseList.add(dto);
        }
        return boardResponseList;
    }
}


