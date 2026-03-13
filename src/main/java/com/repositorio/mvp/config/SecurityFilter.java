package com.repositorio.mvp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.repositorio.mvp.repository.InvalidatedTokenRepository;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.TokenService;

import java.io.IOException;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null){
            if(invalidatedTokenRepository.existsById(token)){
                filterChain.doFilter(request, response);
                return;
            }
            var userIdOpt = tokenService.validateToken(token);
            if (userIdOpt.isPresent()) {
                UUID userId = userIdOpt.get();
                UserDetails user = userRepository.findById(userId).orElse(null);

                if(user != null){
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return null;
        }

        
        return authHeader.replace("Bearer ", "");
    }
}
