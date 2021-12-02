package com.poludnikiewicz.bugtracker.security;


import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static com.poludnikiewicz.bugtracker.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService userService;


    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);

        auth.inMemoryAuthentication()
                .withUser("Admin").password(passwordEncoder.encode("adminPass")).roles("ADMIN")
                //.and().withUser("User").password("userPassword").roles("USER")
                //.and().withUser("Engineer").password("engineerPassword").roles("STAFF")
                .and().passwordEncoder(passwordEncoder);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("manage/api/**").hasRole(ADMIN.name())
                //.mvcMatchers("/api/manage").hasRole("STAFF")
                //.mvcMatchers("/api/bugtracker/*").hasRole("USER")
                .mvcMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()//.headers().frameOptions().disable()
                //.and()
                .formLogin()
                //.loginPage("/login.html") //NOT EXISTING YET!
                //.loginProcessingUrl("/perform_login")
                //.defaultSuccessUrl("/homepage.html", false) // false =user redirected to previous page they wanted to visit before being prompted to authenticate.
                //.failureUrl("/login.html?error=true")
                //.failureHandler(authenticationFailureHandler())
               // .and()
               // .logout()
               // .logoutUrl("/perform_logout")
               // .deleteCookies("JSESSIONID")
                //.logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .httpBasic();

    }

//    @Bean
//    public PasswordEncoder getEncoder() {
//        return new BCryptPasswordEncoder();
//    } commented because I provided password encoder as a private field

//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder);
//        provider.setUserDetailsService(userService);
//        return provider;
//    }


}
