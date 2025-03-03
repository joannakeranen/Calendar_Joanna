import java.io.{FileInputStream, FileOutputStream, FileWriter}
import scala.io.Source
import net.fortuna.ical4j.model.{Calendar, Component, Property}
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.model.property.immutable.*
import net.fortuna.ical4j.data.{CalendarBuilder, CalendarOutputter}
import org.joda.time.DateTime
import scalafx.collections.ObservableBuffer

import java.nio.file.Paths
import java.util.UUID
import scala.collection.mutable.*
import scala.jdk.CollectionConverters.*

import org.joda.time.{DateTime, DateTimeConstants}
import org.joda.time.format.DateTimeFormat
import com.github.nscala_time.time.Imports.*



class FileHandler(calendarHandler: CalendarHandler) {

    //The events (and holidays) that are read from Events.ics and Holidays.ics files are stored here
    //class CalendarHandler calls on FileHandler to use this list
    val eventsList = ListBuffer[Map[String, String]]()
    val holidaysList = ListBuffer[Map[String, String]]()

    //a method to read iCal files
    def readIcsFile(filePath: String) =
      val fileInputStream = new FileInputStream(filePath)
      val calendarBuilder = new CalendarBuilder()
      val calendar = calendarBuilder.build(fileInputStream)
      fileInputStream.close()
      val components = calendar.getComponents

      for component: Component <- components.asScala do
        val eventProperties = Map[String, String]()
        for property <- component.getProperties().asScala do
          val name = property.getName()
          val value = property.getValue()
          eventProperties += name -> value
        eventsList += eventProperties

    //a method to read Events file (iCal file)
    //calls the method readIcsFile
    def readEventsFile() =
      val filePath = "src/Events.ics"
      readIcsFile(filePath)

    //a method to read Holidays file (iCal file)
    //changes the endDate to startDate, because in Holidays file the end date is the day after the holiday
    def readHolidaysFile() =
      val filePath = "src/Holidays.ics"
      val fileInputStream = new FileInputStream(filePath)
      val calendarBuilder = new CalendarBuilder()
      val calendar = calendarBuilder.build(fileInputStream)
      fileInputStream.close()
      val components = calendar.getComponents

      for component: Component <- components.asScala do
        val properties = component.getProperties().asScala

        val dtStart = properties.find( i => i.getName() == "DTSTART").map( j => j.getValue()).getOrElse("")

        val eventProperties = Map[String, String]()
        for property <- component.getProperties().asScala do
          val name = property.getName()
          val value = property.getValue()
          if name == "DTEND" then
            val correctDtEnd = dtStart
            eventProperties += name -> correctDtEnd
          else
            eventProperties += name -> value
        holidaysList += eventProperties

    //a method to write events to Events file (iCal file)
    //is called when save events button is clicked in the gui
    def writeToEventsFile() =
      val calendar = new Calendar()

      val properties = Buffer[Property]()
      properties += new ProdId("-//Joanna Keränen//Calendar 1.0//EN")
      properties += ImmutableVersion.VERSION_2_0
      properties += ImmutableCalScale.GREGORIAN
      calendar.addAll(properties.asJavaCollection)

      for event <- calendarHandler.eventsBuffer do
        val name = event.name
        val category = event.category

        var formattedStartDt: String = null
        var formattedEndDt: String = null

        if event.startTime == "" && event.endTime == "" then
          formattedStartDt = event.startDate
          formattedEndDt = event.endDate
        else
          formattedStartDt = event.startDate + "T" + event.startTime
          formattedEndDt = event.endDate + "T" + event.endTime

        val uidGenerator = new Uid

        val vevent = new VEvent()
        vevent.add(new DtStart(formattedStartDt))
        vevent.add(new DtEnd(formattedEndDt))
        vevent.add(new Summary(name))
        vevent.add(new Categories(category))
        vevent.add(new Uid(UUID.randomUUID().toString))
        calendar.add(vevent)

      val fileOutputStream = new FileOutputStream("src/Events.ics")
      val calendarOutputter = new CalendarOutputter()
      calendarOutputter.output(calendar, fileOutputStream)
      fileOutputStream.close()


    //method to read the Categories file (a text file)
    val categoriesLines = ObservableBuffer[String]()
    def readCategoriesFile() =
      val bufferedSource = Source.fromFile("src/Categories.txt")
      for line <- bufferedSource.getLines do
        categoriesLines += line
      bufferedSource.close

    //method to write to the Categories file (a text file)
    def writeToCategoriesFile(newCategory: String) =
      val fileWriter = new FileWriter("src/Categories.txt", true)
      fileWriter.write("\n" + newCategory)
      fileWriter.close()
  }


@main def testFileHandler =
  val calendarHandler = CalendarHandler()
  val fileHandler = FileHandler(calendarHandler)
  fileHandler.writeToCategoriesFile("Hobbies")
  fileHandler.readCategoriesFile()
  fileHandler.writeToEventsFile()
  fileHandler.readEventsFile()
  fileHandler.readHolidaysFile()
