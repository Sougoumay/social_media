package sougoumay.fr.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sougoumay.fr.social_media.entity.Post;
import sougoumay.fr.social_media.entity.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    @Query("SELECT p FROM Post p WHERE p.author IN ?1 ORDER BY p.createdAt DESC")
    List<Post> findByAuthorsOrderByCreatedAtDesc(List<User> authors);
}