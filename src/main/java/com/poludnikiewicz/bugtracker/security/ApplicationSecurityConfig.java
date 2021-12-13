package com.poludnikiewicz.bugtracker.security;


import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import lombok.AllArgsConstructor;
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
// ^^ this enables using @PreAuthorize annotation replacing mvc/antMatchers
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService userService;
    

    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                //.mvcMatchers(HttpMethod.DELETE, "manage/api/**").hasRole(ADMIN.name())
                //.mvcMatchers("manage/api/**").hasAnyRole(ADMIN.name(), STAFF.name())
                //.mvcMatchers("/api/manage").hasRole("STAFF")
                //.mvcMatchers("/api/bugtracker/*").hasRole("USER")
                .mvcMatchers("/api/registration/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and()
                //.csrf().disable()//.headers().frameOptions().disable()
                //.and()
                .formLogin()
                .loginPage("/login").permitAll()
                //.loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard", false)
                .and()
                .rememberMe()// false =user redirected to previous page they wanted to visit before being prompted to authenticate.
                //.failureUrl("/login.html?error=true")
                //.failureHandler(authenticationFailureHandler())
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");

                //.logoutSuccessHandler(logoutSuccessHandler())
       // .httpBasic();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());

        auth.inMemoryAuthentication()
                .withUser("Admin").password(passwordEncoder.encode("adminPass"))
                .roles("ADMIN");
                //.and().withUser("User").password("userPassword").roles("USER")
               // .and().withUser("Engineer").password("engineerPassword").roles("STAFF")
                //.and().passwordEncoder(passwordEncoder);
    }

    /**
     * An AuthenticationProvider implementation that retrieves user details from a UserDetailsService.
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }


}
