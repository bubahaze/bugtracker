package com.poludnikiewicz.bugtracker.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {

    //@Autowired
    //private UserRepository userRepository;
    private final ApplicationUserDao applicationUserDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    //qualifier in case of multiple implementations
    public ApplicationUserService(ApplicationUserDao applicationUserDao,
                                  PasswordEncoder passwordEncoder) {
        this.applicationUserDao = applicationUserDao;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return applicationUserDao.findByEmail(email)
                .orElseThrow(() ->new UsernameNotFoundException(email + " not found" ));
    }

    public String signUpUser(ApplicationUser user) {
        boolean userExists = applicationUserDao
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists) {
            throw new IllegalStateException("Looks like someone already uses this email");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        applicationUserDao.save(user);
        return "it works";
    }

//    public void save(User user) {
//        userRepository.save(user);
//    } uncommented because of the change from userRepository to ApplicationUserDao
}
