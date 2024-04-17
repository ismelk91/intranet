package deltma.solutions.backend.controllers;

import deltma.solutions.backend.dto.NewsDTO;
import deltma.solutions.backend.services.NewsService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-news")
    public ResponseEntity<String> createNewsArticle(@RequestBody NewsDTO newsDTO) {
        try {
            newsService.createNewsArticle(newsDTO);
            return ResponseEntity.ok("News article created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating news article " + e.getMessage());
        }
    }

    @GetMapping("/get-news")
    public ResponseEntity<List<NewsDTO>> getNews() {
        // Assume newsService is a service class that fetches news articles
        List<NewsDTO> newsList = newsService.getNews();
        return ResponseEntity.ok(newsList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-news")
    public ResponseEntity<?> deleteNewsArticle(@RequestParam Long id) {
        try {
            newsService.deleteNewsById(id);
            return ResponseEntity.ok("News article deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting news article: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit-news/{id}")
    public ResponseEntity<?> editNews(@PathVariable Long id, @Valid @RequestBody NewsDTO request) {
        try {
            Long editedNewsId = newsService.editNews(id, request);
            return ResponseEntity.ok().body(editedNewsId);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error editing news article: " + e.getMessage());
        }
    }
}
