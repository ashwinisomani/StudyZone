package in.toralabs.library.security;

import in.toralabs.library.jpa.repository.JwtTokenRepository;
import in.toralabs.library.service.impl.CustomerUserDetailServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private CustomerUserDetailServiceImpl customerUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String authToken = null, username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authToken = authorizationHeader.substring(7);
            boolean isAuthTokenPresentInDB = jwtTokenRepository.findById(authToken).isPresent();
            try {
                if (isAuthTokenPresentInDB && !jwtTokenRepository.findById(authToken).get().isEnabled()) {
                    logger.info("Disabled authToken is " + authToken);
                    throw new Exception("The token is disabled");
                }
                username = jwtUtil.extractUsername(authToken);
            } catch (ExpiredJwtException e) {
                e.printStackTrace();
                logger.info("Expired authToken is " + authToken);
                if (isAuthTokenPresentInDB) {
                    jwtTokenRepository.disableJwtTokenId(authToken);
                }
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customerUserDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        filterChain.doFilter(request, response);
    }
}
