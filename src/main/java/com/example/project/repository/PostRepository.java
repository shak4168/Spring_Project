package com.example.project.repository;


import com.example.project.domain.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PostRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Post> findAll() {
        return em.createQuery("select p from Post p order by p.createdAt desc", Post.class).getResultList();
    }

    public Post find(Long id) {
        return em.find(Post.class, id);
    }

    public void save(Post post) {
        if (post.getId() == null) em.persist(post);
        else em.merge(post);
    }

    public void delete(Long id) {
        Post p = em.find(Post.class, id);
        if (p != null) em.remove(p);
    }
}