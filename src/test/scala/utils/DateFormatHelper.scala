package utils

import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}

/**
 * Created by Dmitry on 7/8/2015.
 */
object DateFormatHelper {
  private val formatter = ISODateTimeFormat.basicDateTime()

  def dateToString(dateTime: DateTime): String = formatter.print(dateTime)

  def stringToDate(str: String): DateTime = formatter.parseDateTime(str)
}
