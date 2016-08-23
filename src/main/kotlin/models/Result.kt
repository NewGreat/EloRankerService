package models

/**
 * Created by william on 8/22/16.
 */
enum class Result(val i: Int) {
    P1_WIN(1),
    DRAW(0),
    P1_LOSS(-1);

    companion object {
        fun FromInt(i: Int) : Result {
            return when (i) {
                1 -> P1_WIN
                0 -> DRAW
                -1 -> P1_LOSS
                else -> throw Exception("Unable to parse Result")
            }
        }
    }
}