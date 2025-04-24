package com.example.bookms.demo;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="book_details")
public class Book {
	    @Id	
	    private String isbn;
	    private String title;
	    private String author;
	    private LocalDate publishedDate;
	    private int totalCopies;
	    private int issuedCopies;
	    
		public String getIsbn() {
			return isbn;
		}
		public void setIsbn(String isbn) {
			this.isbn = isbn;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public LocalDate getPublishedDate() {
			return publishedDate;
		}
		public void setPublishedDate(LocalDate publishedDate) {
			this.publishedDate = publishedDate;
		}
		public int getTotalCopies() {
			return totalCopies;
		}
		public void setTotalCopies(int totalCopies) {
			this.totalCopies = totalCopies;
		}
		public int getIssuedCopies() {
			return issuedCopies;
		}
		public void setIssuedCopies(int issuedCopies) {
			this.issuedCopies = issuedCopies;
		}  

}

