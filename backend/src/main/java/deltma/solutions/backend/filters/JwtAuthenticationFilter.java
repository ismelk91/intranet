package deltma.solutions.backend.filters;

import deltma.solutions.backend.services.JwtService;
import deltma.solutions.backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts incoming requests, extracts and validates the JWT token,
 * and sets up the authentication context if the token is valid. */

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter intercepting request...");
        // Extracts the Authorization header from the HTTP request.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Checks if the Authorization header is missing or not formatted as expected.
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extracts the JWT token from the Authorization header.
        jwt = authHeader.substring(7);
        System.out.println("JWT: " + jwt);

        // Extracts the user's email from the JWT.
        userEmail = jwtService.extractUserName(jwt);

        // Checks if the user's email is not empty and if the user is not already authenticated.
        if (StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loads user details from the database using the user's email.
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

            // Checks if the JWT token is valid for the user.
            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("User: " + userDetails);// logs user details for debugging purposes.

                // Sets up the user's authentication details in the security context.
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        // Continue with the filter chain.
        filterChain.doFilter(request, response);
    }
}