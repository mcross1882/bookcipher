import java.io.PrintWriter
import scala.io.Source
import scala.util.Random

object Application {

    val randomGenerator = Random

    def main(args: Array[String]) {
        val writer = new PrintWriter("sample/encrypted.txt")
        val decrypted = Source.fromFile("sample/decrypted.txt")
        var cipher = Source.fromFile("sample/cipher.txt").mkString
        var codedIndex: Int = randomIndex(cipher)

        for (letter <- decrypted) {
            // If the cipher does not contain a character in the 
            // source data then we need to halt and notify the user
            if (!cipher.contains(letter)) {
                throw new Exception(s"Cipher does not contain the character $letter")
            }

            // Randomly search for a matching index value to use as a "code"
            // This code is then transformed into a string containing
            // the line, word, and column of the letter for that given index
            while (!isMatchingIndex(letter, codedIndex, cipher)) {
                codedIndex = randomIndex(cipher)
            }
            prettyPrint(codedIndex, cipher, writer)
        }

        writer.flush
        writer.close
    }

    def isMatchingIndex(letter: Char, index: Int, buffer: String): Boolean = letter equals buffer(index)

    def randomIndex(buffer: String): Int = randomGenerator.nextInt(buffer.length)

    /**
     * Here we convert our buffer index (i.e. 76) into a string
     * containing the line, word, and column where that index is located
     * the format is as follows
     * [line]-[word]-[column] ex. 4-12-6
     */
    def prettyPrint(indexToSeek: Int, buffer: String, writer: PrintWriter) {
        var line = 1
        var word = 1
        var letter = 1
        var index = 0
        var currentChar = '\0'

        while (index < indexToSeek) {
            currentChar = buffer(index)
            if ('\n' equals currentChar) {
                line += 1
                word = 1
                letter = 1
            } else if (!currentChar.isSpaceChar && !currentChar.isLetter) {
                // noop these characters are not considered words but
                // we want to continue incrementing the counters
            } else if (currentChar.isSpaceChar) {
                word += 1
                letter = 1
            } else {
                letter += 1
            }
            index += 1
        }

        writer.write("%d-%d-%d%n".format(line, word, letter))
    }
}
