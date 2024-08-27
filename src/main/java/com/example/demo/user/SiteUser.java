package com.example.demo.user;

import com.example.demo.review.Review;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import com.example.demo.question.Question;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private Integer age;

    private String phone;

    @ManyToMany
    public Set<Question> Helped;

    @ManyToMany
    public Set<Question> Requested;

    @ManyToMany
    public Set<Question> Accepted;

    @OneToMany
    private List<Review> review;
}
