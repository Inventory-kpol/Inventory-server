package kpol.Inventory.domain.board.repository;

import kpol.Inventory.domain.board.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByNameIn(List<String> tags);
}
