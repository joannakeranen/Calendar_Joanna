import scala.collection.mutable.*
import com.github.nscala_time.time.Imports.*
import scalafx.beans.property.BooleanProperty
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class CalendarHandler {

  val fileHandler = FileHandler(this)

  //information of events, holidays and reminders are stored in these collections
  val eventsBuffer = Buffer[Event]()
  val holidaysBuffer = Buffer[Event]()
  val remindersBuffer = Buffer[String]()
  val eventsToAddToCurrentWeek = Buffer[(String, Int, Int, String, String, String)]()


  //a method to get the events of the current week including the holidays
  //the method adds these events to teh eventsToAddToCurrentWeek buffer
  def getEventsToAddToCurrentWeek(dateHandler2: DateHandler) =
    fileHandler.readEventsFile()
    fileHandler.readHolidaysFile()

    eventsToAddToCurrentWeek.clear()

    for event <- fileHandler.eventsList do
      var eventCategory: String = null
      val eventName = event("SUMMARY")
      if event.contains("CATEGORIES") then
        eventCategory = event("CATEGORIES")
      if event("DTSTART").contains("T") && event("DTEND").contains("T") then
        val dtStart = event("DTSTART")
        val dtEnd = event("DTEND")
        val eventStartDate = dtStart.split("T").headOption.getOrElse("")
        val eventStartTime = dtStart.split("T").lastOption.getOrElse("")
        val eventEndDate = dtEnd.split("T").headOption.getOrElse("")
        val eventEndTime = dtEnd.split("T").lastOption.getOrElse("")
        val newEvent = Event(eventName, eventStartDate, eventEndDate, eventStartTime, eventEndTime, eventCategory)
        if eventsBuffer.contains(newEvent) then
          println("Already contains this event")
        else
          eventsBuffer += newEvent
      else
        val eventStartDate = event("DTSTART")
        val eventEndDate = event("DTEND")
        val eventStartTime = ""
        val eventEndTime = ""
        val newEvent = Event(eventName, eventStartDate, eventEndDate, eventStartTime, eventEndTime, eventCategory)
        if eventsBuffer.contains(newEvent) then
          println("Already contains this event")
        else
          eventsBuffer += newEvent

    for holiday <- fileHandler.holidaysList do
      var eventCategory: String = null
      val eventName = holiday("SUMMARY")
      val eventStartDate = holiday("DTSTART")
      val eventEndDate = holiday("DTEND")
      val eventStartTime = ""
      val eventEndTime = ""
      val newEvent = Event(eventName, eventStartDate, eventEndDate, eventStartTime, eventEndTime, eventCategory)
        if holidaysBuffer.contains(newEvent) then
          println("Already contains this event")
        else
          holidaysBuffer += newEvent

    var startToCompare: LocalDate = null
    var endToCompare: LocalDate = null
    var startDateInDateTime: DateTime = null
    var endDateInDateTime: DateTime = null

    for event <- eventsBuffer do
      try{
        if event.startTime == "" && event.endTime == "" then
          val start = event.startDate
          val end = event.endDate
          val formatter = DateTimeFormat.forPattern("yyyyMMdd")
          startDateInDateTime = formatter.parseDateTime(start)
          endDateInDateTime = formatter.parseDateTime(end)
          startToCompare = startDateInDateTime.toLocalDate()
          endToCompare = endDateInDateTime.toLocalDate()
        else
          val start = event.startDate + event.startTime
          val end = event.endDate + event.endTime
          val formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss")
          startDateInDateTime = formatter.parseDateTime(start)
          endDateInDateTime = formatter.parseDateTime(end)
          startToCompare = startDateInDateTime.toLocalDate()
          endToCompare = endDateInDateTime.toLocalDate()

        if (startToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && startToCompare.isBefore(dateHandler2.currentSunday.plusDays(1))) && (endToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && endToCompare.isBefore(dateHandler2.currentSunday.plusDays(1)))  then
          val name = event.name
          //the start and end dates as a numbers (mon=1, ..., sun=7)
          val startDayOfWeek = startDateInDateTime.getDayOfWeek
          val endDayOfWeek = endDateInDateTime.getDayOfWeek
          val startTime = event.startTime
          val endTime = event.endTime
          val category = event.category
          val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
          if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
            println("Already in the list")
          else
            eventsToAddToCurrentWeek += eventInfoToAdd
        else if (startToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && startToCompare.isBefore(dateHandler2.currentSunday.plusDays(1))) then
          val name = event.name
          //the start and end dates as a numbers (mon=1, ..., sun=7)
          val startDayOfWeek = startDateInDateTime.getDayOfWeek
          val endDayOfWeek = 7
          val startTime = event.startTime
          var endTime: String = null
          if !(event.endTime == "") then
            endTime = "230000"
          else
            endTime = event.endTime
          val category = event.category
          val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
          if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
            println("Already in the list")
          else
            eventsToAddToCurrentWeek += eventInfoToAdd
        else if (endToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && endToCompare.isBefore(dateHandler2.currentSunday.plusDays(1)))
          val name = event.name
          //the start and end dates as a numbers (mon=1, ..., sun=7)
          val startDayOfWeek = 1
          val endDayOfWeek = endDateInDateTime.getDayOfWeek
          val endTime = event.endTime
          var startTime: String = null
          if !(startTime == "") then
            startTime = "000000"
          else
            startTime = event.startTime
          val category = event.category
          val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
          if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
            println("Already in the list")
          else
            eventsToAddToCurrentWeek += eventInfoToAdd
      }
      catch{
        case ex: Throwable =>
          val errorMessage = "Make sure that the input is in correct format"
          val alert = new Alert(AlertType.Error):
            title = "Error"
            contentText = errorMessage
          alert.showAndWait()
      }

    for holiday <- holidaysBuffer do
      val start = holiday.startDate
      val end = holiday.endDate
      val formatter = DateTimeFormat.forPattern("yyyyMMdd")
      startDateInDateTime = formatter.parseDateTime(start)
      endDateInDateTime = formatter.parseDateTime(end)
      startToCompare = startDateInDateTime.toLocalDate()
      endToCompare = endDateInDateTime.toLocalDate()


      if (startToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && startToCompare.isBefore(dateHandler2.currentSunday.plusDays(1))) && (endToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && endToCompare.isBefore(dateHandler2.currentSunday.plusDays(1)))  then
        val name = holiday.name
        //the start and end dates as a numbers (mon=1, ..., sun=7)
        val startDayOfWeek = startDateInDateTime.getDayOfWeek
        val endDayOfWeek = endDateInDateTime.getDayOfWeek
        val startTime = holiday.startTime
        val endTime = holiday.endTime
        val category = holiday.category
        val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
        if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
          println("Already in the list")
        else
          eventsToAddToCurrentWeek += eventInfoToAdd
      else if (startToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && startToCompare.isBefore(dateHandler2.currentSunday.plusDays(1))) then
        val name = holiday.name
        //the start and end dates as a numbers (mon=1, ..., sun=7)
        val startDayOfWeek = startDateInDateTime.getDayOfWeek
        val endDayOfWeek = 7
        val startTime = holiday.startTime
        var endTime: String = null
        if !(holiday.endTime == "") then
          endTime = "230000"
        else
          endTime = holiday.endTime
        val category = holiday.category
        val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
        if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
          println("Already in the list")
        else
          eventsToAddToCurrentWeek += eventInfoToAdd
      else if (endToCompare.isAfter(dateHandler2.currentMonday.minusDays(1)) && endToCompare.isBefore(dateHandler2.currentSunday.plusDays(1)))
        val name = holiday.name
        //the start and end dates as a numbers (mon=1, ..., sun=7)
        val startDayOfWeek = 1
        val endDayOfWeek = endDateInDateTime.getDayOfWeek
        val endTime = holiday.endTime
        var startTime: String = null
        if !(startTime == "") then
          startTime = "000000"
        else
          startTime = holiday.startTime
        val category = holiday.category
        val eventInfoToAdd = (name, startDayOfWeek, endDayOfWeek, startTime, endTime, category)
        if eventsToAddToCurrentWeek.contains(eventInfoToAdd) then
          println("Already in the list")
        else
          eventsToAddToCurrentWeek += eventInfoToAdd


  //a method to add a new event to the eventsBuffer
  def addNewEvent(name: String, startDate: String, endDate: String, startTime: String, endTime: String, category: String, reminder: Boolean) =
    if reminder then
      remindersBuffer += name
    val parsedStartDate = startDate.replaceAll("\\.", "")
    val parsedEndDate = endDate.replaceAll("\\.", "")
    if startTime == "ALLDAY" && endTime == "ALLDAY" then
      val parsedStartTime = ""
      val parsedEndTime = ""
      val newEvent = Event(name, parsedStartDate, parsedEndDate, parsedStartTime, parsedEndTime, category)
      eventsBuffer += newEvent
    else
      val parsedStartTime = startTime.replaceAll("\\.", "") + "00"
      val parsedEndTime = endTime.replaceAll("\\.", "") + "00"
      val newEvent = Event(name, parsedStartDate, parsedEndDate, parsedStartTime, parsedEndTime, category)
      eventsBuffer += newEvent

  //a method to delete an event from eventsBuffer
  def deleteEvent(name: String, startDate: String, endDate: String, startTime: String, endTime: String, category: String) =
    val parsedStartDate = startDate.replaceAll("\\.", "")
    val parsedEndDate = endDate.replaceAll("\\.", "")
    val parsedStartTime = startTime.replaceAll("\\.", "") + "00"
    val parsedEndTime = endTime.replaceAll("\\.", "") + "00"
    val newEvent = Event(name, parsedStartDate, parsedEndDate, parsedStartTime, parsedEndTime, category)
    if eventsBuffer.contains(newEvent) then
      eventsBuffer -= newEvent
}

@main def testCalendarHandler() =
  val calendarHandler = CalendarHandler()
  calendarHandler.addNewEvent("Jotain", "2024.04.16", "2024.04.16", "ALLDAY", "ALLDAY", "School", true)
  calendarHandler.deleteEvent("Jotain", "2024.04.16", "2024.04.16", "ALLDAY", "ALLDAY", "School")