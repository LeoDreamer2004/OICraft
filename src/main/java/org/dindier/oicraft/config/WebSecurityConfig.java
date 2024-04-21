package org.dindier.oicraft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public WebSecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Ensure the passwords are encoded properly
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // Notice: Create a user temporarily here
        manager.createUser(User.builder().
                username("user").password(passwordEncoder().encode("111"))
                .roles("USER").build());
        return manager;
    }

    /**
     * Set the security filters
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/submission/**").authenticated()
                        .requestMatchers("/problem/*/submit").authenticated()
                        .anyRequest().permitAll()
                ) // Permit all requests to any endpoint temporarily
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .permitAll()
                ) // redirect to the login page
                .logout(logout -> logout
                        .deleteCookies("remove")
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )  // logout, delete cookies
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("rememberMe")
                )  // use cookie to remember the user
        ;
        return http.build();
    }

    /**
     * Set the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Set the Chinese configuration
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:org/springframework/security/messages_zh_CN");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Set the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}