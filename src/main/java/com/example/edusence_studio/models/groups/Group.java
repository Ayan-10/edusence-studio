package com.example.edusence_studio.models.groups;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "groups")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Group extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    /**
     * Helps understand why the group exists
     * Example: LANGUAGE_BARRIER, ABSENTEEISM
     */
    @Column(nullable = false)
    private String focusProblemTag;

    /**
     * Who created this group (Training Professional / Admin)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<GroupMember> teachers = new HashSet<>();
}
