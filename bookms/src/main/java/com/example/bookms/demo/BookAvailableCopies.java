package com.example.bookms.demo;

//Projections selecting only two fields from the table
public interface BookAvailableCopies {
	public String getBookIsbn();
	public int getAvailableCopies();
}
