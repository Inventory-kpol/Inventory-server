package kpol.Inventory.domain.board.repository;

import kpol.Inventory.domain.member.entity.LikeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {
    Optional<LikeBoard> findByMemberIdAndBoardId(Long memberId, Long boardId);
}
