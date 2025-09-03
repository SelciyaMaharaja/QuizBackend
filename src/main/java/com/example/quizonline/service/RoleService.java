package com.example.quizonline.service;

import com.example.quizonline.model.Role;
import com.example.quizonline.model.User;
import com.example.quizonline.exception.RoleAlreadyExistException;
import com.example.quizonline.exception.UserAlreadyExistsException;
import com.example.quizonline.repository.RoleRepository;
import com.example.quizonline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        String roleName = "ROLE_"+theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        if(roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistException(theRole.getName()+" role already exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUserFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if(role.isPresent() && role.get().getUsers().contains(user.get())) {
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

// ...existing code...
@Override
public User assignRoleToUser(Long userId, Long roleId) {
    Optional<User> user = userRepository.findById(userId);
    Optional<Role> role = roleRepository.findById(roleId);

    if (!user.isPresent()) {
        throw new UsernameNotFoundException("User not found");
    }
    if (!role.isPresent()) {
        throw new RoleAlreadyExistException("Role not found");
    }
    if (user.get().getRoles().contains(role.get())) {
        throw new UserAlreadyExistsException(user.get().getEmail() + " is already assigned to the " + role.get().getName() + " role");
    }

    role.get().assignRoleToUser(user.get());
    roleRepository.save(role.get());
    return user.get();
}

    @Override
    public Role removeAllUserFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role :: removeAllUserFromRole);
        return roleRepository.save(role.get());
    }

}