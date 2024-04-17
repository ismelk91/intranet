package deltma.solutions.backend.filters;

import deltma.solutions.backend.services.AzureBlobStorageService;
import deltma.solutions.backend.services.JwtService;
import deltma.solutions.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AzureBlobStorageFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final AzureBlobStorageService azureBlobStorageService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String pictureUrl = azureBlobStorageService.storePicture(userDetails.getUsername(), request.getInputStream());
                log.info("Picture stored at: {}", pictureUrl);
            } else {
                // Log or print debug information about the unexpected principal type
                log.warn("Unexpected principal type: {}", principal.getClass().getName());
            }
        }

        filterChain.doFilter(request, response);
    }
}