package kpol.Inventory.domain.comment.service;


import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.board.repository.BoardRepository;
import kpol.Inventory.domain.comment.dto.req.CommentRequestDto;
import kpol.Inventory.domain.comment.dto.res.CommentResponseDto;
import kpol.Inventory.domain.comment.entity.Comment;
import kpol.Inventory.domain.comment.repository.CommentRepository;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.global.exception.CustomException;
import kpol.Inventory.global.exception.ErrorCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    //댓글 작성 및 저장 기능
    public CommentResponseDto createComment(Long boardId, Member member, CommentRequestDto requestDto) { //댓글 작성 기능

        // 게시글과 회원 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));


        //대댓글 작성시, 부모 댓글 확인
        Comment parentComment = null; //디폹트 값이 null, 대댓글인 경우에만 부모 댓글 객체 할당
        if (requestDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        //댓글 작성
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .createdAt((LocalDateTime.now()))
                .updatedAt(null)
                .member(member)
                .board(board)
                .parentComment(parentComment)
                .build();

        //댓글 저장
        Comment saveComment = commentRepository.save(comment);

        //응답 DTO 생성
        return new CommentResponseDto(
                saveComment.getId(),
                saveComment.getContent(),
                saveComment.getCreatedAt(),
                saveComment.getUpdatedAt(),
                saveComment.getMember().getId(),
                saveComment.getBoard().getId(),
                saveComment.getParentComment() != null ? saveComment.getParentComment().getId() :null,
                new ArrayList<>()
        );
    }

    @Transactional(readOnly = true)
    // 댓글과 대댓글 구조 구현
    public List<CommentResponseDto> getCommentByBoard(Long boardId) {
        // 게시글에 속한 댓글 목록 조회
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        return comments.stream()
                .filter(comment -> comment.getParentComment() == null) //부모 댓글만 필터링
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        comment.getMember().getId(),
                        comment.getBoard().getId(),
                        comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                        getReplies(comment.getId())
                ))
                .collect(Collectors.toList());
    }

    //대댓글 목록 조회 (재귀적)
    private List<CommentResponseDto> getReplies(Long parentCommentId) {
        List<Comment> replies = commentRepository.findByParentCommentId(parentCommentId);
        return replies.stream()
                .map(reply -> new CommentResponseDto(
                        reply.getId(),
                        reply.getContent(),
                        reply.getCreatedAt(),
                        reply.getUpdatedAt(),
                        reply.getMember().getId(),
                        reply.getBoard().getId(),
                        reply.getParentComment() != null ? reply.getParentComment().getId() : null,
                        getReplies(reply.getId()) //대댓글 조회 (재귀).
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    // 댓글 수정 기능
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto){
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 내용 수정
        comment.updateContent(requestDto.getContent());

        // 수정된 댓글 저장
        Comment updatedComment = commentRepository.save(comment);

        // 응답 DTO 생성 , 수정된 댓글 정보를 dto로 변환 후 반환
        return new CommentResponseDto(
                updatedComment.getId(),
                updatedComment.getContent(),
                updatedComment.getCreatedAt(),
                updatedComment.getUpdatedAt(),
                updatedComment.getMember().getId(),
                updatedComment.getBoard().getId(),
                updatedComment.getParentComment() != null ? updatedComment.getParentComment().getId() : null,
                getReplies(updatedComment.getId())
        );


    }

    @Transactional
    // 댓글 삭제 기능
    public Boolean deleteComment(Long commentId, Member member) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 삭제
        commentRepository.delete(comment);
        return true;
    }


}
