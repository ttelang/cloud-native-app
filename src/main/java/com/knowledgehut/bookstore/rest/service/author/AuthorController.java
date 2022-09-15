package com.knowledgehut.bookstore.rest.service.author;

import com.knowledgehut.bookstore.rest.service.author.exception.AuthorNotFoundException;
import com.knowledgehut.bookstore.rest.service.book.Book;
import com.knowledgehut.bookstore.rest.service.book.BookController;
import com.knowledgehut.bookstore.rest.service.book.BookRepository;
import com.knowledgehut.bookstore.rest.service.book.exception.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
public class AuthorController {

    Logger logger = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @GetMapping(value = "/api/v1/authors")
    public ResponseEntity<CollectionModel<EntityModel<Author>>> getAllAuthors() {

        logger.info("GET /api/v1/authors ...");

        List<Author> authors = authorRepository.findAll();
        List<EntityModel<Author>> authorList = new ArrayList<>();

        CollectionModel<EntityModel<Author>> collection;

        authors.forEach(author -> {
            EntityModel<Author> resource = assembleAuthorEntityModel(author);
            authorList.add(resource);
        });

        collection = CollectionModel.of(authorList);
        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).getAllAuthors());
        collection.add(linkSelf.withRel("self"));

        return ResponseEntity.ok(collection);
    }


    @GetMapping(value="/api/v1/authors/{id}")
    public EntityModel<Author> getAuthor(@PathVariable Integer id) {

        logger.info("GET /employees/" + id + " ...");
        Author author = authorRepository.getById(id);
        if (!authorRepository.existsById(id)){
            logger.error("Employee with id-" + id + " not found");
            throw new AuthorNotFoundException("id-" + id);
        }

        return assembleAuthorEntityModel(author);
    }
    public EntityModel<Author> assembleAuthorEntityModel(Author author) {
        EntityModel<Author> resource = EntityModel.of(author);

        WebMvcLinkBuilder linkSelf = linkTo(methodOn(this.getClass()).getAuthor(author.getId()));
        resource.add(linkSelf.withRel("self"));

        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAllAuthors());
        resource.add(linkTo.withRel("authors"));
        return resource;
    }

    @DeleteMapping("/api/v1/authors/{id}")
    public ResponseEntity<Author> deleteAuthor(@PathVariable Integer id) {
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/authors")
    public ResponseEntity<Author> createUser(@RequestBody @Valid Author author) {
        Author newAuthor = authorRepository.save(author);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAuthor.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/api/v1/authors/{id}/books")
    public List<Book> getAuthorBooks(@PathVariable int id){
        Optional<Author> author = authorRepository.findById(id);
        if (author == null) {
            throw new AuthorNotFoundException("id-" + id);
        }

        return author.get().getBooks();
    }

    @PutMapping(value = "/api/v1/authors")
    @ResponseBody
    public ResponseEntity<Object> updateAuthor(@RequestBody Author author) {
        Integer id = author.getId();
        logger.info("PUT /api/v1/authors/ ...");
        if (!bookRepository.existsById(id)) {
            logger.error("Author with id-" + id + " not found");
            throw new BookNotFoundException("id-" + id);
        }
        Author updateAuthor = authorRepository.save(author);
        return ResponseEntity.ok().body(assembleAuthorEntityModel(updateAuthor));
    }
}
