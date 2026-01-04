package com.example.edusence_studio.models.modules;

import com.example.edusence_studio.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private MainModule mainModule;
}


