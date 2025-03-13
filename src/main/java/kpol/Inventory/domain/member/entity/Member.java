package kpol.Inventory.domain.member.entity;

import jakarta.persistence.*;
import kpol.Inventory.domain.board.entity.Board;
import kpol.Inventory.domain.comment.entity.Comment;
import kpol.Inventory.domain.member.dto.req.SignupRequestDto;
import kpol.Inventory.domain.member.enums.MemberRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> likeBoard = new ArrayList<>();

    public Member(SignupRequestDto signupRequestDto, String encodedPassword) {
        this.username = signupRequestDto.getUsername();
        this.nickname = signupRequestDto.getNickname();
        this.email = signupRequestDto.getEmail();
        this.password = encodedPassword;
        this.role = MemberRole.USER;
    }

    public void updateInfo(String nickname, String password){
        this.nickname = nickname;
        this.password = password;
    }
}
