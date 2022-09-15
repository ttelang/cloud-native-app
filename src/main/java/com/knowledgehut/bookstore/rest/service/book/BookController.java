package com.knowledgehut.bookstore.rest.service.book;

import com.knowledgehut.bookstore.rest.service.author.Author;
import com.knowledgehut.bookstore.rest.service.author.AuthorController;
import com.knowledgehut.bookstore.rest.service.book.exception.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@RestController
public class BookController {
    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorController authorController;

    @GetMapping(value = "/api/v1/books")
    public ResponseEntity<CollectionModel<EntityModel<Book>>> getAllBooks() {
        logger.info("GET /api/v1/books ...");
        List<Book> books = bookRepository.findAll();
        List<EntityModel<Book>> bookList = new ArrayList<>();

        books.forEach(book -> {
            EntityModel<Book> resource = assembleBookEntityModel(book);
            bookList.add(resource);
        });

        CollectionModel<EntityModel<Book>> collection = CollectionModel.of(bookList);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).getAllBooks());
        collection.add(linkSelf.withRel("self"));

        return ResponseEntity.ok(collection);
    }

    @PostMapping(value = "/api/v1/books")
    @ResponseBody
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {
        logger.info("POST /api/v1/books/ ...");

        if (bookRepository.existsById(book.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Book newBook = bookRepository.save(book);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newBook.getId()).toUri();

        return ResponseEntity.created(location).body(assembleBookEntityModel(newBook));
    }

    @PutMapping(value = "/api/v1/books/")
    @ResponseBody
    public ResponseEntity<Object> updateBook(@RequestBody Book book) {
        logger.info("PUT /api/vi/books ...");

        Integer id = book.getId();
        if (!bookRepository.existsById(id)) {
            logger.error("Employee with id-" + id + " not found");
            throw new BookNotFoundException("id-" + id);
        }
        Book updateBook = bookRepository.save(book);
        return ResponseEntity.ok().body(assembleBookEntityModel(updateBook));
    }

    @DeleteMapping("/api/v1/books/{id}")
    public ResponseEntity<Author> deleteBook(@PathVariable Integer id) {
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value="/api/v1/books/{id}")
    public EntityModel<Book> getBook(@PathVariable Integer id) {

        logger.info("GET /api/v1/employees/" + id + " ...");
        Book book = bookRepository.getById(id);
        if (!bookRepository.existsById(id)){
            logger.error("Book with id-" + id + " not found");
            throw new BookNotFoundException("id-" + id);
        }

        return assembleBookEntityModel(book);
    }

    @GetMapping("/api/v1/books/{id}/authors")
    public ResponseEntity<CollectionModel<EntityModel<Author>>> getAuthorList(@PathVariable int id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException("id-" + id);
        }

        Book book = bookOptional.get();
        List<Author> authors = book.getAuthors();
        List<EntityModel<Author>> authorList = new ArrayList<>();

        CollectionModel<EntityModel<Author>> collection;

        authors.forEach(author -> {
            EntityModel<Author> resource = authorController.assembleAuthorEntityModel(author);
            authorList.add(resource);
        });

        collection = CollectionModel.of(authorList);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(authorController.getClass()).getAllAuthors());
        collection.add(linkSelf.withRel("self"));

        return ResponseEntity.ok(collection);
    }

    private EntityModel<Book> assembleBookEntityModel(Book book) {
        EntityModel<Book> resource = EntityModel.of(book);

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).getBook(book.getId()));
        resource.add(linkSelf.withRel("self"));

        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAllBooks());
        resource.add(linkTo.withRel("employees"));
        return resource;
    }
}
