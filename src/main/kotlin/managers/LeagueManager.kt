package managers

import dataClasses.models.Rating
import org.joda.time.DateTime

/**
 * Created by william on 9/2/16.
 */

fun GetRatingsForLeagueOnDate(leagueId: Int, dateTime: DateTime) : List<Rating> {
    return repositories.GetRatingsForLeagueOnDate(leagueId, dateTime)
}