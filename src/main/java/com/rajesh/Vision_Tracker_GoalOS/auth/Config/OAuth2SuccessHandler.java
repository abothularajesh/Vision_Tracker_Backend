package com.rajesh.Vision_Tracker_GoalOS.auth.Config;

import com.rajesh.Vision_Tracker_GoalOS.auth.repository.UserRepo;
import com.rajesh.Vision_Tracker_GoalOS.auth.entity.User;
import com.rajesh.Vision_Tracker_GoalOS.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private JwtService jwtService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(
            UserRepo userRepository,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String registrationId =
                ((OAuth2AuthenticationToken) authentication)
                        .getAuthorizedClientRegistrationId();

        String email =
                oauth2User.getAttribute("name");
        String name=oauth2User.getAttribute("name");
        System.out.println(name);
        System.out.println(email);

        String providerId;

        if (registrationId.equals("google")) {
            providerId = oauth2User.getAttribute("sub");
        } else {
            providerId = String.valueOf(
                    oauth2User.getAttribute("id")
            );
        }

        User user = userRepository
                .findByProviderAndProviderId(
                        registrationId.toUpperCase(),
                        providerId
                )
                .orElseGet(() -> {

                    User newUser = new User();

                    newUser.setUsername(name);
                    newUser.setEmail(name);
                    newUser.setProvider(
                            registrationId.toUpperCase()
                    );
                    newUser.setProviderId(providerId);
                    newUser.setRole("USER");

                    return userRepository.save(newUser);
                });

        String token =
                jwtService.generateToken(user.getUsername());

        String redirectUrl =
                frontendUrl+"/oauth2/success"
                        + "?token=" + URLEncoder.encode(
                        token,
                        StandardCharsets.UTF_8
                )
                        + "&name=" + URLEncoder.encode(
                        user.getUsername(),
                        StandardCharsets.UTF_8
                )
                        + "&role=" + URLEncoder.encode(
                        user.getRole(),
                        StandardCharsets.UTF_8
                );

        response.sendRedirect(redirectUrl);
    }
}
