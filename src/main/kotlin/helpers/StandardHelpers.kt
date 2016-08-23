package helpers

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

/**
 * Created by william on 8/20/16.
 */

private val dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")

fun ParseDateTime(dateTimeString: String) : DateTime {
    return dateFormatter.withZone(DateTimeZone.UTC).parseDateTime(dateTimeString)
}

fun IsPositiveInteger(str: String) : Boolean {
    val length = str.length;
    if (length == 0) {
        return false;
    }
    val cArray = str.toCharArray()
    for (c in cArray) {
        if (c < '0' || c > '9') {
            return false
        }
    }
    return true
}