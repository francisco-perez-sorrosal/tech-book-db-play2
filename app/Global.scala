import play.api._

import models._
import anorm._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() = {
    
    if(Book.findAll.isEmpty) {
      
      Seq(
        Book(NotAssigned, "Test Book", Some("1st"), Some("My Books S.A."), Some("2011"), Some("Great book!"), Some(date("2011-11-18")))
      ).foreach(Book.insert)
            
    }
    
  }
  
}