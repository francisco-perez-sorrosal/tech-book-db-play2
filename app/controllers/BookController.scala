package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.validation.Constraints._

import anorm._

import views._
import models._

/**
 * Manage books
 */
object BookController extends Controller { 
    
  /**
   * This result directly redirect to the application home
   */
  val Home = Redirect(routes.BookController.list(0, 2, ""))
	
  /**
   * Describe the book form (used in both edit and create screens).
   */ 
  val bookForm = Form(
    of(Book.apply _)(
      "id" -> ignored(NotAssigned),
      "name" -> requiredText,
	  "edition" -> optional(text(0, 5)),
	  "editorial"-> optional(text),
	  "year" -> optional(text(4, 4)),
	  "remark" -> optional (text),
      "introduced" -> optional(date("yyyy-MM-dd"))
    )
  )
  
  // -- Actions

  /**
   * Handle default path requests, redirect to book list
   */  
  def index = Action { Home }
  
  /**
   * Display the paginated list of books.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on book names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.book.list(
      Book.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
  
  /**
   * Display the 'edit form' of a existing Book.
   *
   * @param id Id of the book to edit
   */
  def edit(id: Long) = Action {
    Book.findById(id).map { book =>
      Ok(html.book.editForm(id, bookForm.fill(book)))
    }.getOrElse(NotFound)
  }
  
  /**
   * Handle the 'edit form' submission 
   *
   * @param id Id of the book to edit
   */
  def update(id: Long) = Action { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.book.editForm(id, formWithErrors)),
      book => {
        Book.update(id, book)
        Home.flashing("success" -> "Book %s has been updated".format(book.name))
      }
    )
  }
  
  /**
   * Display the 'new book form'.
   */
  def create = Action {
    Ok(html.book.createForm(bookForm))
  }
  
  /**
   * Handle the 'new book form' submission.
   */
  def save = Action { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.book.createForm(formWithErrors)),
      book => {
        Book.insert(book)
        Home.flashing("success" -> "Book %s has been created".format(book.name))
      }
    )
  }
  
  /**
   * Handle book deletion.
   */
  def delete(id: Long) = Action {
    Book.delete(id)
    Home.flashing("success" -> "Book has been deleted")
  }

}
