package kpol.Inventory.domain.comment.repository;

import kpol.Inventory.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
    List<Comment> findByParentCommentId(Long parentCommentId);
}
