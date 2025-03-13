package kpol.Inventory.domain.board.repository;

import kpol.Inventory.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select " + "distinct b "
            + "from Board b "
            + "LEFT JOIN b.member m "
            + "LEFT JOIN b.comments c "
            + "LEFT JOIN c.member cm "
            + "WHERE b.title like %:keyWord% "
            + "or b.content like %:keyWord% "
            + "or m.username like %:keyWord% "
            + "or c.content like %:keyWord% "
            + "or cm.username like %:keyWord% ")
    Page<Board> findBoardsByKeyword(@Param("keyWord") String keyWord, Pageable pageable);

    @Query("select " + "distinct b "
            + "from Board b "
            + "LEFT JOIN b.boardTags bt "
            + "LEFT JOIN bt.tag t  "
            + "WHERE t.name like %:boardTag%")
    Page<Board> findBoardByBoardTag(@Param("boardTag") String boardTag, Pageable pageable);

    @Query("SELECT b FROM Board b ORDER BY b.createdAt DESC ")
    List<Board> find3ByOrderByCreatedAtDesc(Pageable pageable);
}
