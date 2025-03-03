case class Event(name: String, startDate: String, endDate: String, startTime: String, endTime: String, category: String) {
  override def equals(obj: Any): Boolean =
    obj match
      case other: Event =>
        this.name == other.name &&
        this.startDate == other.startDate &&
        this.endDate == other.endDate &&
        this.startTime == other.startTime &&
        this.endTime == other.endTime &&
        this.category == other.category
      case _ => false
}
