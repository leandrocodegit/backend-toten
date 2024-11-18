package br.com.totem.security;

import br.com.totem.model.User;
import br.com.totem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndStatus(email, true).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return new UserDetailsImpl(user);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = new User();
//        user.setEmail("admin");
//        user.setPassword("$2a$10$Ra/VAlbHDFTC0r6wJ6k76uZsIdBvbthCpZHUEdtlvCYJMps/Ntygy");
//        user.setRoles(Arrays.asList(Role.ADMIN, Role.USER));
//        return new UserDetailsImpl(user);
//    }

}