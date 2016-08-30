package managers

import dataClasses.models.Tournament
import repositories.InsertTournament

/**
 * Created by william on 8/30/16.
 */

fun CreateTournament(tournament: Tournament) {
    InsertTournament(tournament)
}