package contracts

/**
 * Created by william on 8/28/16.
 */
data class AddPlayerCommand (
    val UserId: Int?,
    val LeaguePlayerName: String,
    val JoinDate: String
)
