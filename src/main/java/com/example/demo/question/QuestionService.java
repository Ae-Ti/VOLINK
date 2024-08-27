package com.example.demo.question;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import com.example.demo.DataNotFoundException;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.user.SiteUser;

import com.example.demo.answer.Answer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, String date, String place, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setDate(date);
        q.setPlace(place);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content, String date, String place) {
        question.setSubject(subject);
        question.setContent(content);
        question.setDate(date);
        question.setPlace(place);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public void request(Question question, SiteUser siteUser) {
        question.getRequestUser().add(siteUser);
        siteUser.getRequested().add(question);
        this.questionRepository.save(question);
        userRepository.save(siteUser);
    }

    public void accept(Question question, SiteUser siteUser) {
        question.getAcceptUser().add(siteUser);
        question.requestUser.remove(siteUser);
        siteUser.Accepted.add(question);
        siteUser.Requested.remove(question);
        this.questionRepository.save(question);
        userRepository.save(siteUser);
    }

    public void acceptCancel(Question question, SiteUser siteUser) {
        question.getAcceptUser().remove(siteUser);
        question.requestUser.add(siteUser);
        siteUser.Accepted.remove(question);
        siteUser.Requested.add(question);
        this.questionRepository.save(question);
        userRepository.save(siteUser);
    }

    public void complete(Question question, SiteUser siteUser) {
        question.getCompletedUser().add(siteUser);
        question.getAcceptUser().remove(siteUser);
        siteUser.getHelped().add(question);
        siteUser.Accepted.remove(question);
        this.questionRepository.save(question);
        userRepository.save(siteUser);
    }

    public void completeCancel(Question question, SiteUser siteUser) {
        question.getCompletedUser().remove(siteUser);
        question.getAcceptUser().add(siteUser);
        siteUser.getHelped().remove(question);
        siteUser.Accepted.add(question);
        this.questionRepository.save(question);
        userRepository.save(siteUser);
    }
}
