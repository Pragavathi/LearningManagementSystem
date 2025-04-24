package com.example.bookms.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookms.Repository.BookRepo;
import com.example.bookms.demo.Book;
import com.example.bookms.demo.BookAvailableCopies;

@RestController
@RequestMapping("/books")
public class BookResource {
	private static final Logger LOGGER=LoggerFactory.getLogger(BookResource.class);
	
	@Autowired
	private BookRepo bookRepo;
	
	@GetMapping("/welcome")
	public String getMessage()
	{
		LOGGER.info("Getting all Books from the database");
		return "In bookms";
	}

	@GetMapping("")
	public List<Book> getAllBooks()
	{
		LOGGER.info("Getting all Books from the database");
		return bookRepo.findAll();
	}
	
	@GetMapping("/{isbn}")
	public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn)
	{
		LOGGER.info("Getting single book from the database");
		Optional <Book> bookFound=bookRepo.findById(isbn);
		if(bookFound.isPresent())
		{
			LOGGER.info("Book Found with  ISBN {} from Databse",isbn);
			return ResponseEntity.ok(bookFound.get());
		}
		LOGGER.error("Book Not Found for the given {}",isbn );
		return ResponseEntity.notFound().build();
	}
	
	
	@GetMapping("/author/{author}")
	public List<Book> getBookByAuthor(@PathVariable String author)
	{
		LOGGER.info("Retrieve all the books written by {}", author);		
		return bookRepo.findBookByAuthorContainingIgnoreCase(author);
	}
	
	@GetMapping("/title/{title}")
	public List<Book> getBookByTitle(@PathVariable String title)
	{
		LOGGER.info("Retrieve all the books with the title of {}", title);		
		return bookRepo.findBookByTitleContainingIgnoreCase(title);
	}
	@GetMapping("/availableBooks")
	public List<BookAvailableCopies> getAllAvailableCopiesOfBooks()
	{
		LOGGER.info("Getting all  Available Books from the database");
		return bookRepo.findAvailableCopiesOfBooks();
	}
	
	@PostMapping("")
	public ResponseEntity<Book> addBook(@RequestBody Book book)
	{
		LOGGER.info("Saving a Book into database");
		Book savedBook=bookRepo.save(book);
		LOGGER.info("Book saved into database with {}",savedBook.getIsbn());
		return ResponseEntity.created(URI.create(savedBook.getIsbn().toString())).body(savedBook);
	}
	
	@DeleteMapping("/{isbn}")
	public ResponseEntity<Book> deleteBook(@PathVariable String isbn)
	{
		LOGGER.info("Deleting a Book from the database with {}",isbn);
		Optional <Book> bookFound=bookRepo.findById(isbn);
		if(bookFound.isPresent())
		{
			bookRepo.deleteById(isbn);
			LOGGER.info("Book deleted with  ISBN {} from Databse",isbn);
			return ResponseEntity.ok(bookFound.get());
		}
		LOGGER.error("Book Not Found for the given {}",isbn );
		return ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{isbn}")
	public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book book)
	{
		book.setIsbn(isbn);
		LOGGER.info("updating Book with {}",isbn);
		Book savedBook=bookRepo.save(book);
		LOGGER.info(" The given book with {} updated Successfully !!!! ",isbn);
		return ResponseEntity.ok(savedBook);
	}
}

