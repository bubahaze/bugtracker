package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.dao.User;
import com.poludnikiewicz.bugtracker.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserService implements UserDetailsService {

    //@Autowired
    //private UserRepository userRepository;
    private final ApplicationUserDao applicationUserDao;

    @Autowired
    //qualifier in case of multiple implementations
    public ApplicationUserService(@Qualifier("fake") ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return applicationUserDao.selectApplicationUserByUsername(username)
                .orElseThrow(() ->new UsernameNotFoundException(username + " not found" ));
    }

//    public void save(User user) {
//        userRepository.save(user);
//    } uncommented because of the change from userRepository to ApplicationUserDao
}
