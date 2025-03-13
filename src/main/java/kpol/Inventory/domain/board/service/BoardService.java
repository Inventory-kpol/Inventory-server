package kpol.Inventory.domain.board.service;

import jakarta.transaction.Transactional;
import kpol.Inventory.domain.board.dto.req.BoardRequestDto;
import kpol.Inventory.domain.board.dto.req.BoardUpdateRequestDto;
import kpol.Inventory.domain.board.dto.res.BoardDetailResponseDto;
import kpol.Inventory.domain.board.dto.res.BoardListResponseDto;
import kpol.Inventory.domain.board.dto.res.BoardPageResponseDto;
import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.board.entity.BoardTag;
import kpol.Inventory.domain.board.entity.Tag;
import kpol.Inventory.domain.board.repository.BoardRepository;
import kpol.Inventory.domain.board.repository.TagRepository;
import kpol.Inventory.global.exception.CustomException;
import kpol.Inventory.global.exception.ErrorCode;
import kpol.Inventory.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;

    @Transactional
    public BoardDetailResponseDto createBoard(UserDetailsImpl userDetails, BoardRequestDto boardRequestDto) {
        try {
            // 보드 생성
            Board board = new Board(userDetails.getMember(), boardRequestDto.getTitle(), boardRequestDto.getContent());
            log.info("Board created: {}", board);

            log.info("Add Board Tag");
            // 태그 추가
            addTagsList(board ,boardRequestDto.getTags());
            log.info("Board Tag Added");

            boardRepository.save(board);

            return new BoardDetailResponseDto(board);

        } catch (Exception e) {
            log.error("Failed to create board : {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.BOARD_CREATED_FAILED);
        }
    }

    private void addTagsList(Board board, List<String> tags){
        try {
            // 중복 태그 제거
            List<String> uniqueTags = tags.stream().distinct().toList();

            // 존재하는 태그 검색
            List<Tag> existingTags = tagRepository.findAllByNameIn(uniqueTags);
            Set<String> existingTagsName = existingTags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet());
            log.info("existing tags : {}", existingTagsName);

            // 새로운 입력된 태그
            List<Tag> newTags = uniqueTags.stream()
                            .filter(tagName -> !existingTagsName.contains(tagName))
                            .map(Tag::new)
                            .collect(Collectors.toList());
            log.info("new tags : {}", newTags);

            // 업데이트할 태그
            List<Tag> updateTags = new ArrayList<>();
            updateTags.addAll(existingTags);
            if (!newTags.isEmpty()) {
                tagRepository.saveAll(newTags);
                updateTags.addAll(newTags);
            }

            log.info("add tags : {}", updateTags);

            for (Tag tag : updateTags) {
                boolean isAlreadyExist = existingTags.stream()
                        .anyMatch(boardTag -> boardTag.equals(tag));
                log.info("Checking if tag {} is already existed: {}", tag.getName(), isAlreadyExist);

                BoardTag boardTag;

                if (!isAlreadyExist) {
                    boardTag = new BoardTag(board, tag);
                } else {
                    boardTag = board.getBoardTags().stream()
                            .filter(bt -> bt.getTag().equals(tag))
                            .findFirst()
                            .orElse(new BoardTag(board, tag));
                }

                board.getBoardTags().add(boardTag);
                tag.getBoardTags().add(boardTag);
            }
        } catch (Exception e) {
            log.error("Failed to add tags : {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.TAG_ADDED_FAILED);
        }
    }

    public BoardPageResponseDto searchBoard(int page, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new CustomException(ErrorCode.KEYWORD_CANNOT_EMPTY);
        }
        log.info("Board search request Keyword: {}", keyword);

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        // 첫 번째 인자는 몇 번째 페이지에 대한 요청인지를 판단, 페이지는 0부터 시작, 첫 페이지는 0, 두 번째 페이지는 1
        // 두 번째 인자는 한 페이지에 보여줄 항목의 개수
        // 세 번째 인자는 정렬 기준
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        log.info("Board Search Keyword Pageable Setting");

        Page<Board> boardPage = boardRepository.findBoardsByKeyword(keyword, pageable);

        Page<BoardDetailResponseDto> dtoPage = BoardDetailResponseDto.toDtoPage(boardPage);

        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        boardPageResponseDto.setBoards(dtoPage);

        return boardPageResponseDto;
    }

    public BoardPageResponseDto searchBoardByTag(int page, String boardTag) {
        if (boardTag == null || boardTag.isEmpty()) {
            throw new CustomException(ErrorCode.TAG_CANNOT_EMPTY);
        }
        log.info("Board search request HashTag: {}", boardTag);

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        log.info("Board Search HashTag Pageable Setting");

        Page<Board> boardPage = boardRepository.findBoardByBoardTag(boardTag, pageable);

        Page<BoardDetailResponseDto> dtoPage = BoardDetailResponseDto.toDtoPage(boardPage);

        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        boardPageResponseDto.setBoards(dtoPage);

        return boardPageResponseDto;
    }

    public BoardDetailResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        return new BoardDetailResponseDto(board);
    }

    @Transactional
    public BoardDetailResponseDto updateBoard(Long boardId ,BoardUpdateRequestDto boardUpdateRequestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        board.update(boardUpdateRequestDto);
        log.info("Board {} updated",board);

        board.clearBoardTags();
        log.info("Cleared Board Tag");

        addTagsList(board, boardUpdateRequestDto.getBoardTags());
        log.info("Board Tag Updated");

        boardRepository.save(board);

        return new BoardDetailResponseDto(board);
    }

    @Transactional
    public Boolean deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        boardRepository.delete(board);
        log.info("Board {} deleted",board);

        return true;
    }

    public List<BoardListResponseDto> getLatestPosts() {
        Pageable pageable = PageRequest.of(0, 3);

        log.info("Get latest posts");
        List<Board> boards = boardRepository.find3ByOrderByCreatedAtDesc(pageable);
        log.info("Successfully retrieved the latest posts");

        List<BoardListResponseDto> boardListResponseDtos = new ArrayList<>();

        log.info("Create BoardListResponseDto");
        for (Board board : boards) {
            boardListResponseDtos.add(new BoardListResponseDto(board));
        }
        log.info("Successfully created Response");

        return boardListResponseDtos;
    }
}
