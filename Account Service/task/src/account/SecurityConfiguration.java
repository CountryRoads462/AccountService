package account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.PrintWriter;

import java.util.LinkedHashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    UserDetailsService userDetailService;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    EventRepository eventRepo;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyRole(
                                Role.USER.name(),
                                Role.ACCOUNTANT.name(),
                                Role.ADMINISTRATOR.name()
                        )
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole(
                                Role.USER.name(),
                                Role.ACCOUNTANT.name()
                        )
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole(Role.ACCOUNTANT.name())
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole(Role.ACCOUNTANT.name())
                        .requestMatchers(HttpMethod.GET, "/api/admin/user/").hasRole(Role.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.DELETE, "api/admin/user/{username}",
                                "api/admin/user/").hasRole(Role.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasRole(Role.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/access").hasRole(Role.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.GET, "/api/security/events").hasRole(Role.AUDITOR.name())
                        .anyRequest().permitAll()
                )
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();

                    LinkedHashMap<String, Object> errorBody = new LinkedHashMap<>();
                    errorBody.put("status", 403);
                    errorBody.put("error", "Forbidden");
                    errorBody.put("message", "Access Denied!");
                    errorBody.put("path", request.getRequestURI());

                    eventRepo.save(new Event(
                            EventName.ACCESS_DENIED,
                            request.getRemoteUser(),
                            request.getRequestURI(),
                            request.getRequestURI()
                    ));

                    String errorBodyJsonString = gson.toJson(errorBody);

                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(403);
                    out.print(errorBodyJsonString);
                    out.flush();
                })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint);
        return http.build();
    }

    @Transactional
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(getEncoder());
        return authProvider;
    }

    @Transactional
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}