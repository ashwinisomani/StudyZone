package in.toralabs.library.service.impl;

import in.toralabs.library.jpa.model.LoginModel;
import in.toralabs.library.jpa.repository.LoginDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomerUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    LoginDetailRepository loginDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginModel loginModel = loginDetailRepository.findByUserName(username);
        return new User(loginModel.getUserName(), loginModel.getPassword(), new ArrayList<>());
    }
}
