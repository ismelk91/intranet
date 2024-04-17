package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.NewsDTO;
import deltma.solutions.backend.models.News;
import deltma.solutions.backend.models.User;
import deltma.solutions.backend.repositories.NewsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Autowired
    private final NewsRepository newsRepository;

    @Autowired
    private final EmailService emailService;

    public void createNewsArticle(NewsDTO newsDTO) {
        News news = new News(
                newsDTO.getSubject(),
                newsDTO.getMessage(),
                newsDTO.getDate(),
                newsDTO.getDeadline()
        );
        newsRepository.save(news);
    }

    public List<NewsDTO> getNews() {
        // Retrieve all news
        List<News> newsList = newsRepository.findAll();

        System.out.println("message: " + newsList);
        // Convert the list of News entities to a list of NewsDTO objects
        return newsList.stream()
                .map(news -> {
                    NewsDTO newsDTO = new NewsDTO();
                    newsDTO.setId(news.getId());
                    newsDTO.setSubject(news.getSubject());
                    newsDTO.setMessage(news.getMessage());
                    newsDTO.setDate(news.getDate());
                    newsDTO.setDeadline(news.getDeadline());

                    return newsDTO;
                })
                .collect(Collectors.toList());
    }

    public void deleteNewsById(Long id) {
        News news = newsRepository.findNewsById(id);
        if(news != null) {
            newsRepository.delete(news);
        } else {
            throw new IllegalArgumentException("News not found");
        }
    }

    public Long editNews(Long id, NewsDTO newsDTO) {
        // Find the news with the given ID
        News existingNews = newsRepository.findById(id).orElse(null);

        // Check if the news with the given ID exists
        if (existingNews == null) {
            throw new EntityNotFoundException("News with id " + id + " not found");
        }

        // Update the existing news with the new data
        existingNews.setSubject(newsDTO.getSubject());
        existingNews.setMessage(newsDTO.getMessage());
        existingNews.setDate(newsDTO.getDate());
        existingNews.setDeadline(newsDTO.getDeadline());

        // Save the updated news
        News updatedNews = newsRepository.save(existingNews);

        // Return the ID of the updated news
        return updatedNews.getId();
    }


    public void shareNews(String email, NewsDTO newsDTO) {
        News news = newsRepository.findNewsById(newsDTO.getId());

        System.out.println("Retrieved news article: " + news);

        if (news != null) {
            emailService.sendSharedNewsArticle(email, news.getSubject(), news.getMessage());
            System.out.println("Shared news article with email: " + email);
        } else {
            throw new EntityNotFoundException("News with id " + newsDTO + " not found");
        }
    }
}
