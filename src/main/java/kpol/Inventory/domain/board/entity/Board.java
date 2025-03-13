package kpol.Inventory.domain.board.entity;

import jakarta.persistence.*;
import kpol.Inventory.domain.board.dto.req.BoardUpdateRequestDto;
import kpol.Inventory.domain.comment.entity.Comment;
import kpol.Inventory.domain.member.entity.Member;
import kpol.Inventory.domain.ranking.entity.Ranking;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int viewCount;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardTag> boardTags = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> boardImages = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ranking ranking;

    public Board(Member member, String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.viewCount = 0;
        this.likeCount = 0;
        this.member = member;
        this.boardTags = new ArrayList<>();
        this.boardImages = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public void update(BoardUpdateRequestDto boardUpdateRequestDto) {
        this.title = boardUpdateRequestDto.getTitle();
        this.content = boardUpdateRequestDto.getContent();
        this.updatedAt = LocalDateTime.now();
    }

    public void clearBoardTags() {
        this.boardTags.clear();
    }

    public List<String> getTagsName() {
        return boardTags.stream()
                .map(boardTag -> boardTag.getTag().getName())
                .collect(Collectors.toList());
    }

    public void viewBoard() {
        this.viewCount++;
    }

    public void likeBoard() {
        this.likeCount++;
    }
}
