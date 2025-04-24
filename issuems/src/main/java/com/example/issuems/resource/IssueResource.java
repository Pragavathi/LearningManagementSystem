package com.example.issuems.resource;

import com.example.issuems.demo.Book;
import com.example.issuems.demo.BookAvailableCopies;
import com.example.issuems.demo.Issue;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.issuems.Repository.IssueRepo;


import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class IssueResource {
	
	private static Logger LOGGER = LoggerFactory.getLogger(IssueResource.class);
	@Autowired
	private IssueRepo issueRepo;
	
	@Autowired
	private WebClient webClient;
	
	//update the copies of the book which is in bookms microservice
	@CircuitBreaker(name="bookmsclient", fallbackMethod="bookmsFallBack")
	public Book updateBookCopy(Book foundBook)			
	{		
		return webClient.put()
				.uri("/books/{isbn}", foundBook.getIsbn())							
				 .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				 .body(BodyInserters.fromValue(foundBook))
				.retrieve()
				.bodyToMono(Book.class)
				.block();	
	}
	
	//Get the book by id from bookms microservice
	@CircuitBreaker(name="bookmsclient", fallbackMethod="bookmsFallBack")
	public Book getBookById(String isbn)			
	{		
		return webClient.get()
				.uri("/books/{isbn}", isbn)		
				.retrieve()
				.bodyToMono(Book.class)
				.block();	
	}
	
	@GetMapping("/issue-books/availableBooks")
	@CircuitBreaker(name="bookmsclient", fallbackMethod="bookmsFallBack2")
	public Mono<Object> getAvailbleBooks()
	{
		return webClient.get().uri("/books/availableBooks")
				.retrieve()
				.bodyToMono(Object.class);
					
	}
	
	//Get all the issue details
	@GetMapping("/issue-books/customer/{id}")
	public List<Issue> getIssueDetailsByCustomerId(@PathVariable Long id)
	{
		return issueRepo.findByCustomerId(id);
	}
	
	@GetMapping("/issue-books/isbn/{isbn}")
	public List<Issue> getIssueDetailsByIsbn(@PathVariable String isbn)
	{
		return issueRepo.findByIsbn(isbn);
	}
	
	
	@GetMapping("/issue-books")
	public List<Issue> getIssueDetails()
	{
		return issueRepo.findAll();
	}
	
	//Cancelling the particular issue which in turn update the copies of book in bookms as well as delete the entry in issue_details table
	@DeleteMapping("/issue-books/{id}")
	@CircuitBreaker(name="bookmsclient", fallbackMethod="bookmsIssueFallBack")
	public ResponseEntity<String> cancelIssue(@PathVariable Long id)
	{	
		Issue foundIssue;
		Optional<Issue> issue=issueRepo.findById(id);
		if(issue.isPresent())
		{
			foundIssue=issue.get();
			String isbn=foundIssue.getIsbn();		
			Book foundBook = getBookById(isbn);			
			issueRepo.deleteById(id);
			
			int issuedCopies=foundBook.getIssuedCopies()-foundIssue.getNoOfCopies();	
			foundBook.setIssuedCopies(issuedCopies);
			Book updatedCopy=updateBookCopy(foundBook);
				
								
			LOGGER.info("Updated the Book Microservice, Now ISBN: {} Available Copies are {}",foundIssue.getIsbn(),foundBook.getTotalCopies()-foundBook.getIssuedCopies());
	
			LOGGER.info("Successfully Deleted the Issue of book :  to the customer ",foundIssue.getIsbn(), foundIssue.getCustomerId());
			return ResponseEntity.ok("Successfully Deleted the Issue of book "+ foundIssue.getIsbn() + "to the customer " + foundIssue.getCustomerId());
		}
		else
		{
			LOGGER.info("Could not find the specified Issue Id {}",id);
			return ResponseEntity.notFound().build();
		}
	}
	
	// Issue the particular book to customer id by updating issuedcopies in book microservice as well as insert an entry into issue_details
	// Checking the available copies before doing the above operation
	@PostMapping("/issue-books")
	@CircuitBreaker(name="bookmsclient", fallbackMethod="bookmsIssueFallBack")
	public ResponseEntity<String> issueBook(@RequestBody Issue issue)
	{
		String isbn= issue.getIsbn();
		LOGGER.info("Retrieve the book from book micro service {}",isbn);
	
		if(checkBookExists(isbn))
		{
			LOGGER.info("++++++++Book found+++++++");
			Book foundBook = getBookById(isbn);
			
			int availableCopies=foundBook.getTotalCopies()-foundBook.getIssuedCopies();
			if(availableCopies>=issue.getNoOfCopies())
			{
				LOGGER.info("Issuing {} book to {}",issue.getIsbn(), issue.getCustomerId());		
				
				Issue savedIssue=issueRepo.save(issue);	
				
				int totalCopiesIssued=foundBook.getIssuedCopies()+issue.getNoOfCopies();					
				foundBook.setIssuedCopies(totalCopiesIssued);
				Book updatedCopy=updateBookCopy(foundBook);
				
				LOGGER.info( "Total Copies Issued  : {}",totalCopiesIssued);					
				LOGGER.info("Updated the Book Microservice, Now ISBN: {} Available Copies are {}",issue.getIsbn(),foundBook.getTotalCopies()-issue.getNoOfCopies());
	
				LOGGER.info("Issuing ISBN : {} book to Customer : {}  with IssueId:{}",issue.getIsbn(), issue.getCustomerId() ,issue.getId());
				return ResponseEntity.created(URI.create(savedIssue.getId().toString())).body("Book is Issued Successfully to Customer :" + issue.getCustomerId());						
			}
			else
			{
				LOGGER.info("Requested copies of books {} are not available" , issue.getIsbn());
				return ResponseEntity.ok("The Requested copies of book are not available");
			}
		}
		else
		{
			LOGGER.info("++++++++Not found +++++++");
			return ResponseEntity.notFound().build();			
		}
	
	}
	

	//Fall Back Method
	public ResponseEntity<String> bookmsIssueFallBack(CallNotPermittedException ex )
	{
		return ResponseEntity.badRequest().build();
	}
	
	//Fall Back Method
	public Book bookmsFallBack(CallNotPermittedException ex )
	{
			return null;
	}
	//Fall Back Method
		public Mono<Object> bookmsFallBack2(CallNotPermittedException ex )
		{
				return null;
		}

	// Check existence of book in book microservice and return boolean
	private Boolean checkBookExists(String isbn) {
		return webClient.get()
             .uri("/books/{isbn}", isbn) 
             .exchangeToMono(response -> {
            	 if (response.statusCode().is2xxSuccessful()) 
            		 return Mono.just(Boolean.TRUE);
            	 else if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
            		 	return Mono.just(Boolean.FALSE);
            	 }
            	 return Mono.just(Boolean.FALSE);
      }).block();
	}

    	
}
