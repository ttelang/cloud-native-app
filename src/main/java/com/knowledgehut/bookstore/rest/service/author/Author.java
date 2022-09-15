package com.knowledgehut.bookstore.rest.service.author;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knowledgehut.bookstore.rest.service.book.Book;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="AUTHOR")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "BIO")
    private String bio;
    @Column(name = "EMAIL")
    private String email;

    public Author(Integer id, String name, String bio, String email, List<Book> books) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.email = email;
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @ManyToMany(mappedBy = "authors")
    @JsonIgnore
    private List<Book> books = new ArrayList<>();

    public Author() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
