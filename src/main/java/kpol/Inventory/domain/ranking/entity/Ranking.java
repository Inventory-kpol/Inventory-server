package kpol.Inventory.domain.ranking.entity;

import jakarta.persistence.*;
import kpol.Inventory.domain.board.entity.Board;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ranking")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int ranking_value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
