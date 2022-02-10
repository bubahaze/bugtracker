package com.poludnikiewicz.bugtracker.security;


import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService userService;
    private final SimpleAuthenticationSuccessHandler successHandler;


    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/user").hasRole(ApplicationUserRole.USER.name())
                .mvcMatchers("/admin").hasRole(ApplicationUserRole.ADMIN.name())
                .mvcMatchers("/staff").hasRole(ApplicationUserRole.STAFF.name())
                .mvcMatchers("/api/registration/**", "/css/*", "/js/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .successHandler(successHandler)
                .and()
                .rememberMe()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login")
                .and()
                .httpBasic();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());

        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .and().withUser("memouser").password(passwordEncoder.encode("userpass")).roles("USER")
                .and().withUser("staffmem").password(passwordEncoder.encode("staffmem")).roles("STAFF");
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
