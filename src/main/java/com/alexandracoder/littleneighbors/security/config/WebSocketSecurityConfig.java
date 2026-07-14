package com.alexandracoder.littleneighbors.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private static final Pattern MATCH_TOPIC_PATTERN =
            Pattern.compile("^/topic/(?:messages|playdates)/(\\d+)$");
    private final JwtDecoder jwtDecoder;
    private final MatchRepository matchRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(16)
                .maxPoolSize(64)
                .queueCapacity(500);

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    authenticateConnect(accessor);
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    authorizeSubscribe(accessor);
                }
                return message;
            }
        });
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessagingException("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(token);
            accessor.setUser(new JwtAuthenticationToken(jwt));
        } catch (Exception e) {
            throw new MessagingException("Invalid token", e);
        }
    }

    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) return;

        Matcher matcher = MATCH_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) return;

        JwtAuthenticationToken auth = (JwtAuthenticationToken) accessor.getUser();
        if (auth == null) {
            throw new MessagingException("Unauthenticated subscription");
        }

        Long matchId = Long.parseLong(matcher.group(1));
        MatchEntity match = matchRepository.findWithFamiliesById(matchId)
                .orElseThrow(() -> new MessagingException("Match not found"));

        String email = auth.getToken().getSubject();
        String requesterEmail = match.getChildRequest().getFamily().getUser().getEmail();
        String targetEmail = match.getChildTarget().getFamily().getUser().getEmail();

        if (!requesterEmail.equals(email) && !targetEmail.equals(email)) {
            throw new MessagingException("You are not part of this match");
        }
    }
}

