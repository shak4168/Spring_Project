package com.example.project.web;
import com.example.project.domain.Post;
import com.example.project.service.PostService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;
    public PostController(PostService service) { this.service = service; }

    @GetMapping
    public List<Post> list() { return service.list(); }

    @GetMapping("/{id}")
    public Post get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public Post create(@RequestBody Map<String, String> body) {
        Post p = new Post();
        p.setTitle(body.get("title"));
        p.setContent(body.get("content"));
        return service.save(p);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Post p = service.get(id);
        if (p == null) throw new RuntimeException("Not found");
        p.setTitle(body.get("title"));
        p.setContent(body.get("content"));
        return service.save(p);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        service.delete(id);
        return Map.of("ok", "true");
    }
}