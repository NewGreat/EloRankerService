package dataClasses.models

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

        fun ToInt(result: Result) : Int {
            return when (result) {
                P1_WIN -> 1
                DRAW -> 0
                P1_LOSS -> -1
                else -> throw Exception("Unable to map Result")
            }
        }
    }
}