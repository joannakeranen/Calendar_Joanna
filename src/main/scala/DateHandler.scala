import org.joda.time.{DateTime, DateTimeConstants}
import org.joda.time.format.DateTimeFormat
import com.github.nscala_time.time.Imports.*

import scala.collection.mutable


class DateHandler {

  //current date
  val currentDate = LocalDate.now()

  //get the dates of the current week
  var currentMonday = currentDate.withDayOfWeek(DateTimeConstants.MONDAY)
  var currentTuesday = currentMonday.plusDays(1)
  var currentWednesday = currentMonday.plusDays(2)
  var currentThursday = currentMonday.plusDays(3)
  var currentFriday = currentMonday.plusDays(4)
  var currentSaturday = currentMonday.plusDays(5)
  var currentSunday = currentMonday.plusDays(6)


  //get the current week
  var currentWeek = currentDate.getWeekOfWeekyear

  //get the current month
  var currentMonth = currentDate.month.getAsText


  //method to calculate correct month (at least 4 of the days of the week belong to this month)
  def calculateCorrectMonth() =
    if currentMonday.withDayOfWeek(DateTimeConstants.MONDAY).getMonthOfYear == currentThursday.withDayOfWeek(DateTimeConstants.THURSDAY).getMonthOfYear then
      currentMonth = DateTimeFormat.forPattern("MMMM").print(currentMonday)
    else
      currentMonth = DateTimeFormat.forPattern("MMMM").print(currentThursday)

  //method to calculate the days of the following week
  def changeToNextWeek() =
    val currentDays = List(currentMonday, currentTuesday, currentWednesday, currentThursday, currentFriday, currentSaturday, currentSunday)
    val updatedDays = currentDays.map( i => i.plusDays(7))
    currentMonday = updatedDays(0)
    currentTuesday = updatedDays(1)
    currentWednesday = updatedDays(2)
    currentThursday = updatedDays(3)
    currentFriday = updatedDays(4)
    currentSaturday = updatedDays(5)
    currentSunday = updatedDays(6)
    currentWeek = currentWeek + 1


  //method to calculate the days of the previous week
  def changeToPreviousWeek() =
    val currentDays = List(currentMonday, currentTuesday, currentWednesday, currentThursday, currentFriday, currentSaturday, currentSunday)
    val updatedDays = currentDays.map( i => i.minusDays(7))
    currentMonday = updatedDays(0)
    currentTuesday = updatedDays(1)
    currentWednesday = updatedDays(2)
    currentThursday = updatedDays(3)
    currentFriday = updatedDays(4)
    currentSaturday = updatedDays(5)
    currentSunday = updatedDays(6)
    currentWeek = currentWeek - 1
}

@main def test =
  val dateHandler = DateHandler()
  println(dateHandler.currentWeek)
  println(dateHandler.currentMonth)
  println(dateHandler.currentMonday)
  dateHandler.changeToNextWeek()
  println(dateHandler.currentMonday)
  dateHandler.changeToPreviousWeek()
  println(dateHandler.currentMonday)
