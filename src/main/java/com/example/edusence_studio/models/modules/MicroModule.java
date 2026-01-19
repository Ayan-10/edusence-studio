package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MicroModule extends BaseEntity {

    @Column(nullable = false)
    private String title;

    /**
     * e.g. EN, HI, BN
     */
    @Column(nullable = false)
    private String languageCode;

    @Column(nullable = false)
    private String fileUrl;

    /**
     * Optional tag to map with analytics problemTag
     */
    private String focusProblemTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_module_id", nullable = false)
    @JsonIgnore // Prevent circular reference during JSON serialization
    private MainModule mainModule;
}


