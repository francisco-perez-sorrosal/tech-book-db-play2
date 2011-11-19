package models

import java.util.{Date, Locale}
import java.text.{SimpleDateFormat}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Book(id: Pk[Long], 
	name: String, 
	edition: Option[String], 
	editorial: Option[String], 
	year: Option[String], 
	remark: Option[String], 
	introduced: Option[Date])

object Book {

	// -- Parsers

	/**
	* Parse a Book from a ResultSet
	*/
	val simple = {
		get[Pk[Long]]("book.id") ~/
		get[String]("book.name") ~/
		get[Option[String]]("book.edition") ~/
		get[Option[String]]("book.editorial") ~/
		get[Option[String]]("book.year") ~/
		get[Option[String]]("book.remark") ~/
		get[Option[Date]]("book.introduced") ^^ {
			case id~name~edition~editorial~year~remark~introduced => Book(id, name, edition, editorial, year, remark, introduced)
		}
	}
	
	// -- Queries

	/**
	* Retrieve a book from the id.
	*/
	def findById(id: Long): Option[Book] = {
		DB.withConnection { implicit connection =>
			SQL("select * from book where id = {id}").on('id -> id).as(Book.simple ?)
		}
	}
	
	/**
     * Retrieve all books.
     */
  	def findAll: Seq[Book] = {
    	DB.withConnection { implicit connection =>
      		SQL("select * from book").as(Book.simple *)
		}
  	}
		
  	/**
	 * Return a page of Books.
	 *
	 * @param page Page to display
	 * @param pageSize Number of books per page
	 * @param orderBy Book property used for sorting
	 * @param filter Filter applied on the name column
	 */
	def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[Book] = {

	  val offset = pageSize * page

	  DB.withConnection { implicit connection =>

	    val books = SQL(
	      """
	        select * from book
	        where book.name like {filter}
	        order by {orderBy} nulls last
	        limit {pageSize} offset {offset}
	      """
	    ).on(
	      'pageSize -> pageSize, 
	      'offset -> offset,
	      'filter -> filter,
	      'orderBy -> orderBy
	    ).as(Book.simple *)

	    val totalRows = SQL(
	      """
	        select count(*) from book
	        where book.name like {filter}
	      """
	    ).on(
	      'filter -> filter
	    ).as(scalar[Long])

	    Page(books, page, offset, totalRows)

	  }

	}

	/**
	 * Update a book.
	 *
	 * @param id The book id
	 * @param book The book values.
	 */
	def update(id: Long, book: Book) = {
	  DB.withConnection { implicit connection =>
	    SQL(
	      """
	        update book
	        set name = {name}, edition = {edition}, editorial = {editorial}, year = {year}, remark = {remark}
	        where id = {id}
	      """
	    ).on(
	      'id -> id,
	      'name -> book.name,
		  'edition -> book.edition,
		  'editorial -> book.editorial,
		  'year -> book.year,
		  'remark -> book.remark
	    ).executeUpdate()
	  }
	}

	/**
	 * Insert a new book.
	 *
	 * @param book The book values.
	 */
	def insert(book: Book) = {
	  DB.withConnection { implicit connection =>
	    SQL(
	      """
	        insert into book values (
	          (select next value for book_seq), 
	          {name}, {edition}, {editorial}, {year}, {remark}, {introduced}
	        )
	      """
	    ).on(
	      'name -> book.name,
		  'edition -> book.edition,
	      'editorial -> book.editorial,
	      'year -> book.year,
	      'remark -> book.remark,
	      'introduced -> (new SimpleDateFormat("yyyy-MM-dd", new Locale("en","US"))).format(new Date)
	    ).executeUpdate()
	  }
	}

	/**
	 * Delete a book.
	 *
	 * @param id Id of the book to delete.
	 */
	def delete(id: Long) = {
	  DB.withConnection { implicit connection =>
	    SQL("delete from book where id = {id}").on('id -> id).executeUpdate()
	  }
	}

}