import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.layout.{Border, ColumnConstraints, GridPane, HBox, RowConstraints, VBox}
import scalafx.scene.control.{Button, CheckBox, ChoiceBox, Label, TextField}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.Font

import scala.collection.mutable.*


object Main extends JFXApp3:

  val UIwidth = 800
  val UIheight = 600
  val standardPadding = Insets(10, 10, 10, 10)
  val standardSpacing = 5
  //fonts
  val proximaNovaExtrabld = Font.loadFont(getClass.getResourceAsStream("/Fonts/ProximaNova-Extrabld.ttf"), 30)
  val proximaNovaLight = Font.loadFont(getClass.getResourceAsStream("/Fonts/ProximaNova-Light.ttf"), 15)

  val dateHandler = DateHandler()
  val calendar = CalendarHandler()
  val fileHandler = FileHandler(calendar)

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "Calendar"
      width = UIwidth
      height = UIheight

    //root for original (weekly) view
    val root = new GridPane:
      padding = standardPadding
      hgap = standardSpacing
      vgap = standardSpacing

    val mainScene = Scene(parent = root)
    stage.scene = mainScene

    //root for view for adding a new event
    val addEventRoot = new VBox:
      padding = standardPadding
      spacing = standardSpacing

    //root for daily view
    val dailyRoot = new GridPane():
      padding = standardPadding

    //Elements of the weekly view
    val topBox = new HBox():
      padding = Insets(15)
      spacing = standardSpacing
    topBox.setAlignment(Pos.BaselineCenter)

    //sections for the weekday labels
    val days = Buffer[VBox]()
    //sections for the events of weekly view
    val eventsOfDays = Buffer[VBox]()
    for i <- 0 to 7 do
      days += new VBox()
      eventsOfDays += new VBox():
        border = Border.stroke(Black)
    days.foreach( _.setAlignment(Pos.BaselineCenter))

    //adding the sections to the weekly view
    root.add(topBox, 0, 0, 8, 1)

    for i <- 0 to 7 do
      root.add(days(i), i, 1)
      root.add(eventsOfDays(i), i, 2, 1, 2)

    //setting the sizes (as %) of the different sections of the grid
    val weekdaySize = new ColumnConstraints():
      percentWidth = 12.5
    val topBoxSize = new RowConstraints():
      percentHeight = 10
    val weekdayHeight = new RowConstraints():
      percentHeight = 5
    val eventsHeight = new RowConstraints():
      percentHeight = 85

    root.columnConstraints = Array(weekdaySize, weekdaySize, weekdaySize, weekdaySize, weekdaySize, weekdaySize, weekdaySize, weekdaySize)
    root.rowConstraints = Array(topBoxSize, weekdayHeight, eventsHeight)

    //sections of the daily view
    val dailyTopBox = new HBox()
    val allDay = new HBox()
    val allDayEvents = new HBox():
      border = Border.stroke(Black)

    val hours = Buffer[HBox]()
    val hourlyEventSlots = Buffer[HBox]()
    for i <- 0 to 23 do
      hours += new HBox()
      hourlyEventSlots += new HBox():
        border = Border.stroke(Black)

    //method to update events to daily view, takes the weekday of the event (as an integer mon=1, ..., sun=7) as parameter
    def updateDailyView(weekday: Int) =
      calendar.getEventsToAddToCurrentWeek(dateHandler)
      for (name, startDay, endDay, startTime, endTime, category) <- calendar.eventsToAddToCurrentWeek do
        if startTime == "" && endTime == ""  then
          if startDay == weekday || endDay == weekday then
            allDayEvents.children.add(new Label(name))
        else
          val start = startTime.dropRight(4).toInt
          val end = endTime.dropRight(4).toInt
          if startDay == weekday && endDay == weekday then
           for time <- start to end do
              val nameLabel = new Label(name)
              hourlyEventSlots(time).children.add(nameLabel)
          else if startDay == weekday then
            for time <- start to 23 do
              val nameLabel = new Label(name)
              hourlyEventSlots(time).children.add(nameLabel)
          else if endDay == weekday then
            for time <- 0 to end do
              val nameLabel = new Label(name)
              hourlyEventSlots(time).children.add(nameLabel)
          else if startDay < weekday && endDay > weekday then
            allDayEvents.children.add(new Label(name))

    def updateMondayEvents() = updateDailyView(1)
    def updateTuesdayEvents() = updateDailyView(2)
    def updateWednesdayEvents() = updateDailyView(3)
    def updateThursdayEvents() = updateDailyView(4)
    def updateFridayEvents() = updateDailyView(5)
    def updateSaturdayEvents() = updateDailyView(6)
    def updateSundayEvents() = updateDailyView(7)

    //method to clear the daily view
    def clearDailyView() =
      allDayEvents.children.clear()
      hourlyEventSlots.foreach(i => i.children.clear())

    //labels for weekdays
    //when the labels are clicked the calendar switches to the daily view
    val mondayLabel = new Label(dateHandler.currentMonday.getDayOfMonth + " Monday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateMondayEvents()
    val tuesdayLabel = new Label(dateHandler.currentTuesday.getDayOfMonth + " Tuesday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateTuesdayEvents()
    val wednesdayLabel = new Label(dateHandler.currentWednesday.getDayOfMonth + " Wednesday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateWednesdayEvents()
    val thursdayLabel = new Label(dateHandler.currentThursday.getDayOfMonth + " Thursday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateThursdayEvents()
    val fridayLabel = new Label(dateHandler.currentFriday.getDayOfMonth + " Friday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateFridayEvents()
    val saturdayLabel = new Label(dateHandler.currentSaturday.getDayOfMonth + " Saturday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateSaturdayEvents()
    val sundayLabel = new Label(dateHandler.currentSunday.getDayOfMonth + " Sunday"):
      font = proximaNovaLight
      onMouseClicked = (event) =>
        mainScene.root = dailyRoot
        clearDailyView()
        updateSundayEvents()

    //label for reminders
    val remindersLabel = new Label("Reminders"):
      font = proximaNovaLight

    //weekday and reminders label in a buffer
    val weeklyLabels = Buffer(remindersLabel, mondayLabel, tuesdayLabel, wednesdayLabel, thursdayLabel, fridayLabel, saturdayLabel, sundayLabel)

    //labels for current month and week
    val monthLabel = new Label(dateHandler.currentMonth):
      font = proximaNovaExtrabld
      padding = Insets(0, 200, 0, 200)
    val weekLabel = new Label("week " + dateHandler.currentWeek):
      font = proximaNovaLight
      padding = Insets(5, 10, 0, 10)

    //method to add the events of the current week to the GUI
    def updateEventsToWeeklyView() =
      calendar.getEventsToAddToCurrentWeek(dateHandler)
      for (name, startDay, endDay, startTime, endTime, category) <- calendar.eventsToAddToCurrentWeek do
        for day <- startDay to endDay do
          day match
            case 1 => eventsOfDays(1).children.add(new Label(name))
            case 2 => eventsOfDays(2).children.add(new Label(name))
            case 3 => eventsOfDays(3).children.add(new Label(name))
            case 4 => eventsOfDays(4).children.add(new Label(name))
            case 5 => eventsOfDays(5).children.add(new Label(name))
            case 6 => eventsOfDays(6).children.add(new Label(name))
            case 7 => eventsOfDays(7).children.add(new Label(name))

    //method to update reminders to weekly view
    def updateRemindersToWeeklyView() =
      calendar.remindersBuffer.foreach( i => eventsOfDays(0).children.add(new Label(i)))

    //calls the updateEventsToWeeklyView method one time to add the events to the gui when launcing the program
    updateEventsToWeeklyView()

    //method for updating the labels to have the correct dates, week and month
    def updateLabels() =
      mondayLabel.text = dateHandler.currentMonday.getDayOfMonth + " Monday"
      tuesdayLabel.text = dateHandler.currentTuesday.getDayOfMonth + " Tuesday"
      wednesdayLabel.text = dateHandler.currentWednesday.getDayOfMonth + " Wednesday"
      thursdayLabel.text = dateHandler.currentThursday.getDayOfMonth + " Thursday"
      fridayLabel.text = dateHandler.currentFriday.getDayOfMonth + " Friday"
      saturdayLabel.text = dateHandler.currentSaturday.getDayOfMonth + " Saturday"
      sundayLabel.text = dateHandler.currentSunday.getDayOfMonth + " Sunday"
      weekLabel.text = "week " + dateHandler.currentWeek.toString
      monthLabel.text = dateHandler.currentMonth.toString

    //method to clear the boxes for events
    def clearEventsBoxes() =
      eventsOfDays.foreach( i => i.children.clear())

    //buttons of topBox of weekly view
    val addEventButton = new Button("Add/Delete event"):
      font = proximaNovaLight
      onAction = (event) => mainScene.root = addEventRoot
    val saveEventsButton = new Button("Save events"):
      font = proximaNovaLight
      onAction = (event) =>
        fileHandler.writeToEventsFile()
        println("Events saved to file")
    val arrowBack = new Button("<"):
      font = proximaNovaLight
      onAction = (event) =>
        dateHandler.changeToPreviousWeek()
        dateHandler.calculateCorrectMonth()
        updateLabels()
        clearEventsBoxes()
        updateEventsToWeeklyView()
    arrowBack.setStyle("-fx-background-color: transparent")
    val arrowForward = new Button(">"):
      font = proximaNovaLight
      onAction = (event) =>
        dateHandler.changeToNextWeek()
        dateHandler.calculateCorrectMonth()
        updateLabels()
        clearEventsBoxes()
        updateEventsToWeeklyView()
    arrowForward.setStyle("-fx-background-color: transparent")
    //add elements to topBox
    topBox.children.addAll(addEventButton, saveEventsButton, monthLabel, arrowBack, weekLabel, arrowForward)

    //add weekday and reminders labels to weekly view
    for i <- 0 to 7 do
      days(i).children.add(weeklyLabels(i))

    //adding sections to dailyRoot
    dailyRoot.add(dailyTopBox, 0, 0, 2, 1)

    dailyRoot.add(allDay, 0, 1)
    dailyRoot.add(allDayEvents, 1, 1)

    for i <- 0 to 23 do
      dailyRoot.add(hours(i), 0, (i + 2))
      dailyRoot.add(hourlyEventSlots(i), 1, (i + 2))

    //setting the size of sections of daily view
    val dailyHBoxSize = new RowConstraints():
      percentHeight = 4
    val hourSize = new ColumnConstraints():
      percentWidth = 7
    val EventSize = new ColumnConstraints():
      percentWidth = 35

    dailyRoot.rowConstraints = Array(dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize, dailyHBoxSize)
    dailyRoot.columnConstraints = Array(hourSize, EventSize)

    //label for all day events
    val allDayLabel = new Label("All day")

    //creating labels for times
    val timesLabelTexts = Buffer("00.00", "01.00", "02.00", "03.00", "04.00", "05.00", "06.00", "07.00", "08.00", "09.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00", "21.00", "22.00", "23.00")
    val timesLabels = Buffer[Label]()
    for i <- 0 to 23 do
      timesLabels += new Label(timesLabelTexts(i))

    //exit button for daily view
    val exitDaily = new Button("X"):
      font = proximaNovaLight
      onAction = (event) => mainScene.root = root

    //adding elements to sections of daily view
    dailyTopBox.children.addAll(exitDaily)
    allDay.children.addAll(allDayLabel)

    for i <- 0 to 23 do
      hours(i).children.add(timesLabels(i))

    //view for adding an event

    //sections of the addEventView
    val navigationBox = new HBox()
    navigationBox.setAlignment(Pos.CenterRight)
    val nameBox = new HBox()
    val startDateBox = new HBox()
    val endDateBox = new HBox()
    val startTimeBox = new HBox()
    val endTimeBox = new HBox()
    val categoryBox = new HBox()
    val reminderBox = new HBox()
    val createEventBox = new HBox()
    createEventBox.setAlignment(Pos.CenterRight)

    //add sections to the root
    addEventRoot.children.addAll(navigationBox, nameBox, startDateBox, endDateBox, startTimeBox, endTimeBox, categoryBox, reminderBox, createEventBox)

    //elements of the different sections of add event view
    val exit = new Button("X"):
      font = proximaNovaLight
      onAction = (event) => mainScene.root = root

    val nameOfEvent = new Label("Event")
    val nameInput = new TextField():
      prefWidth = 300
      promptText = "Event name"

    val startDateLabel = new Label("Start date")
    val startDateInput = new TextField():
      prefWidth = 300
      promptText = "YYYY.MM.DD"

    val endDateLabel = new Label("End date")
    val endDateInput = new TextField():
      prefWidth = 300
      promptText = "YYYY.MM.DD"

    val startTimeLabel = new Label("Start time")
    val startTimeInput = new TextField():
      prefWidth = 300
      promptText = "00.00"

    val endTimeLabel = new Label("End time")
    val endTimeInput = new TextField():
      prefWidth = 300
      promptText = "00.00"

    //read the categoriesfile to get the categories
    fileHandler.readCategoriesFile()

    val categoryLabel = new Label("Category")
    val categoryInput = new ChoiceBox[String]:
      items = fileHandler.categoriesLines
    val newCategoryLabel = new Label("Add a new category")
    val newCategoryInput = new TextField():
      prefWidth = 300
      promptText = "Category name"
    val addNewCategoryButton = new Button("Add new category"):
      onAction = (event) =>
        fileHandler.writeToCategoriesFile(newCategoryInput.text.value)
        fileHandler.categoriesLines += newCategoryInput.text.value

    val reminderLabel = new Label("Reminder")
    val reminderInput = new CheckBox()

    val createEventButton = new Button("Create event"):
      onAction = (event) =>
        calendar.addNewEvent(nameInput.text.value, startDateInput.text.value, endDateInput.text.value, startTimeInput.text.value, endTimeInput.text.value, categoryInput.value.value, reminderInput.isSelected)
        clearEventsBoxes()
        updateEventsToWeeklyView()
        updateRemindersToWeeklyView()
        mainScene.root = root

    val deleteEventButton = new Button("Delete event"):
      onAction = (event) =>
        calendar.deleteEvent(nameInput.text.value, startDateInput.text.value, endDateInput.text.value, startTimeInput.text.value, endTimeInput.text.value, categoryInput.value.value)
        clearEventsBoxes()
        updateEventsToWeeklyView()
        mainScene.root = root

    //adding the elements to their sections
    navigationBox.children.addAll(exit)
    nameBox.children.addAll(nameOfEvent, nameInput)
    startDateBox.children.addAll(startDateLabel, startDateInput)
    endDateBox.children.addAll(endDateLabel, endDateInput)
    startTimeBox.children.addAll(startTimeLabel, startTimeInput)
    endTimeBox.children.addAll(endTimeLabel, endTimeInput)
    categoryBox.children.addAll(categoryLabel, categoryInput, newCategoryLabel, newCategoryInput, addNewCategoryButton)
    reminderBox.children.addAll(reminderLabel, reminderInput)
    createEventBox.children.addAll(createEventButton, deleteEventButton)

  end start

end Main