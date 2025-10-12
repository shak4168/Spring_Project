package com.example.project.service;

import com.example.project.domain.Post;
import com.example.project.repo.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PostService {
    private final PostRepository repo;
    public PostService(PostRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Post> list() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Post get(Long id) { return repo.find(id); }

    @Transactional
    public Post save(Post p) { repo.save(p); return p; }

    @Transactional
    public void delete(Long id) { repo.delete(id); }
}