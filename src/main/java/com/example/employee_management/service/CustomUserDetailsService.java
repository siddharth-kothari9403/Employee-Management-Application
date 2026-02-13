package com.example.employee_management.service;

import com.example.employee_management.exceptions.UserNotFoundException;
import com.example.employee_management.model.CustomUserDetails;
import com.example.employee_management.repository.RoleRepository;
import com.example.employee_management.repository.UserRepository;
import com.example.employee_management.entity.Role;
import com.example.employee_management.entity.User;
import com.example.employee_management.model.UserDTO;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @NullUnmarked
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new CustomUserDetails(user);
    }

    public User getUserById(Integer id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with id: "+id);
        }
        return user.get();
    }

    public User saveUser(UserDTO user) {
        System.out.println("Adding user");
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findRoleByName("USER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        newUser.setRoles(roleSet);

        return userRepository.save(newUser);
    }

    public User saveHR(UserDTO user) {
        System.out.println("Adding HR");
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findRoleByName("HR_MANAGER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        newUser.setRoles(roleSet);

        return userRepository.save(newUser);
    }

    public User saveAdmin(UserDTO user){
        System.out.println("Adding admin");
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findRoleByName("ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }
}