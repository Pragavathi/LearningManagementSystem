# LibraryManagementSystem

 ● 4 Microservices
 1. API Gateway (Resource Server)-  Port 8080
 2. Eureka Server  (Service Registry) - Port 8761
 3. Bookms (Book Microservice) - Port 8082
 4. Issuems( Issue Microservice) - Port 8083
 
  H2 In-Memory Database is used

 1. API Gateway - for routing and cross cutting concerns
     Through the API Gateway only we are accessing bookms and issuems microservices
    
 2. Eureka Server - All the services are registered automatically in eureka server which provides discovery of services.
    
 3. Book Microservice - (Bookms)
   Which stores the Bookdetails of a library
   APIs used to fetch/post/delete/update
     Get Methods
    
           http://lcoalhost:8080/bookms/books - Fetch all the books
    
           http://lcoalhost:8080/bookms/books/{isbn} - Fetch book by isbn
    
           http://lcoalhost:8080/bookms/books/author/{author} - Fetch book by author
    
           http://lcoalhost:8080/bookms/books/title/{title} - Fetch book by title
    
           http://lcoalhost:8080/bookms/books/availbleBooks - Fetch the available books by checking total copies against issued copies
    
     Post Methods
    
           http://lcoalhost:8080/bookms/books - Add a new Book

     Put Method
    
           http://lcoalhost:8080/bookms/books/{isbn} - edit the  book by isbn

     Delete Method

           http://lcoalhost:8080/bookms/books/{isbn} - Delete book by isbn

    
 4. Issue Microservice - (Issuems)
     Which stores the issued book details of a library
     APIs used to fetch/post/delete/update
     Get Methods
    
           http://lcoalhost:8080/issuems/issue-books - Fetch all the issued details of books

           http://lcoalhost:8080/issuems/books/{isbn} - Fetch  issued details of a particular book

           http://lcoalhost:8080/issuems/issue-books/availbleBooks - Fetch all the available books by calling bookms

           http://lcoalhost:8080/issuems/issue-books/customer/{customerid} - To fetch the details of the books issued to the customer

           http://lcoalhost:8080/issuems/issue-books/isbn/{isbn} - To fetch the details of particular issued book

     Post Methods
    
           http://lcoalhost:8080/issuems/issue-books - storing Issued detail

     Delete Method

          http://lcoalhost:8080/issuems/issue-books/{id} - Delete the issue details when cancelling/returning the book
