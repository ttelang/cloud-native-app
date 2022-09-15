package com.knowledgehut.bookstore.rest.service.book;

import com.knowledgehut.bookstore.rest.service.author.Author;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "BOOK")
public class Book {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TITLE")
    String title;

    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "IMAGE")
    String image;
    @Column(name = "ISBN")
    String isbn;

    @Column(name = "PAGE_COUNT")
    Integer pageCount;
    @Column(name = "PUBLISHED_DATE")
    Date publishedDate;

    public Book() {
    }

    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(name = "book_author",
        joinColumns = @JoinColumn(name="author_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"))
    private List<Author> authors = new ArrayList<>();

    public Book(Integer id, String title, String description, String image, String isbn, Integer pageCount, Date publishedDate, List<Author> authors) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.publishedDate = publishedDate;
        this.authors = authors;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
