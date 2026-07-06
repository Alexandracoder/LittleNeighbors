package com.alexandracoder.littleneighbors.security.config;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
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

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Autenticación y autorización a nivel de STOMP.
 *
 * - CONNECT: exige un JWT válido en la cabecera Authorization. Si falta o es
 *   inválido, se rechaza la conexión (antes se dejaba pasar como anónima).
 * - SUBSCRIBE a /topic/messages/{matchId}: solo se permite si el usuario
 *   autenticado es una de las dos familias del match. Sin este control,
 *   cualquier cliente conectado podía suscribirse a un matchId ajeno y leer
 *   los mensajes de otra conversación (fuga de datos entre familias).
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private static final Pattern MATCH_TOPIC_PATTERN = Pattern.compile("^/topic/messages/(\\d+)$");

    private final JwtDecoder jwtDecoder;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    authenticateConnect(accessor);
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    authorizeSubscribe(accessor);
                }

                return message;
            }
        });
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessagingException("Missing or malformed Authorization header on WebSocket CONNECT");
        }

        String token = authHeader.substring(7);

        try {
            Jwt jwt = jwtDecoder.decode(token);
            accessor.setUser(new JwtAuthenticationToken(jwt));
        } catch (Exception e) {
            throw new MessagingException("Invalid or expired token on WebSocket CONNECT", e);
        }
    }

    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }

        Matcher matcher = MATCH_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            // No es una suscripción a un chat de match concreto: no aplica esta regla.
            return;
        }

        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new MessagingException("Unauthenticated subscription attempt to " + destination);
        }

        Long matchId = Long.valueOf(matcher.group(1));

        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new MessagingException("User not found for subscription"));

        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MessagingException("Match not found: " + matchId));

        UserEntity requester = match.getRequestUser();
        UserEntity target = match.getTargetUser();

        boolean belongsToMatch =
                (requester != null && requester.getId().equals(user.getId())) ||
                (target != null && target.getId().equals(user.getId()));

        if (!belongsToMatch) {
            throw new MessagingException(
                    "User " + user.getId() + " is not authorized to subscribe to match " + matchId);
        }
    }
}
