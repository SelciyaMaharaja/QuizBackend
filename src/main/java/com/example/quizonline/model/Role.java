package com.example.quizonline.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    public void removeUserFromRole(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }

    public void assignRoleToUser(User user) {
        this.users.add(user);
        user.getRoles().add(this);
    }

    public void removeAllUserFromRole() {
        for (User user : users) {
            user.getRoles().remove(this);
        }
        this.users.clear();
    }
}
