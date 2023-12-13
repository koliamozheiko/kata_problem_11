package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByUsername(email);
        if (user == null ) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", email));
        }
        return user;
    }
    @Override
    public User findUserById(Long id) {
        return  userRepository.findById(id).get();
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }
    @Override
    @Transactional
    public boolean saveDefaultUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return true;
        }

        user.addRole(roleService.findByName("ROLE_USER"));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return false;
    }
    @Override
    public boolean deleteUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        if (!user.getPassword().equals(findUserById(user.getId()).getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
        return true;
    }

}
