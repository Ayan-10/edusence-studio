package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import com.example.edusence_studio.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MainModule extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @OneToMany(mappedBy = "mainModule", cascade = CascadeType.ALL)
    private List<MicroModule> microModules = new ArrayList<>();
}


