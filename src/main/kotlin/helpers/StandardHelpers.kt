package helpers

/**
 * Created by william on 8/20/16.
 */

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