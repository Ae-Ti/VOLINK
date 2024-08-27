package com.example.demo.review;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.demo.user.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", length=200)
    private String content;

    private Integer grade;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteUser siteUser;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;
}

