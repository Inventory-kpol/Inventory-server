package kpol.Inventory.domain.board.repository;

import kpol.Inventory.domain.board.entity.BoardTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {
}
