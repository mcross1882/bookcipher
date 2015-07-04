import java.io.PrintWriter
import scala.io.Source
import scala.util.Random

object Application {

    def main(args: Array[String]) {
        val writer = new PrintWriter("sample/encrypted.txt")
        val decrypted = Source.fromFile("sample/decrypted.txt")
        val cipher = Source.fromFile("sample/cipher.txt").mkString
        val bookCipher = new BookCipher(cipher)

        for (letter <- decrypted) {
            writer.write("%s%n".format(bookCipher.encode(letter).toString))
        }

        writer.flush
        writer.close

        Source.fromFile("sample/encrypted.txt").getLines.foreach { line =>
            print(bookCipher.decode(Cipher.fromLine(line)))
        }
    }


    object Cipher {
        def fromLine(line: String): Cipher = {
            val params = line.split("-").map(_.toInt)
            Cipher(params(0), params(1), params(2))
        }
    }

    case class Cipher(line: Int, word: Int, column: Int) {
        override def toString() = s"$line-$word-$column"
    }

    class BookCipher(buffer: String) {

        private val randomGenerator = Random

        def encode(letter: Char): Cipher = {
            // If the cipher does not contain a character in the
            // source data then we need to halt and notify the user
            if (!buffer.contains(letter)) {
                throw new Exception(s"Cipher does not contain the character $letter")
            }

            // Randomly search for a matching index value to use as a "code"
            // This code is then transformed into a string containing
            // the line, word, and column of the letter for that given index
            var codedIndex = 0
            while (!isMatchingIndex(letter, codedIndex)) {
                codedIndex = randomIndex
            }

            var line = 1
            var word = 1
            var column = 1
            var currentChar = '\0'
            var currentIndex = 0

            while (currentIndex < codedIndex) {
                currentChar = buffer(currentIndex)
                if ('\n' equals currentChar) {
                    line += 1
                    word = 1
                    column = 1
                } else if (!currentChar.isSpaceChar && !currentChar.isLetter) {
                    // noop these characters are not considered words but
                    // we want to continue incrementing the counters
                } else if (currentChar.isSpaceChar) {
                    word += 1
                    column = 1
                } else {
                    column += 1
                }
                currentIndex += 1
            }

            Cipher(line, word, column)
        }

        def decode(cipher: Cipher): Char = {
            var line = 1
            var currentIndex = 0
            while (line != cipher.line) {
                if ('\n' equals buffer(currentIndex)) {
                    line += 1
                }
                currentIndex += 1
            }

            var word = 1
            while (word != cipher.word) {
                if (buffer(currentIndex).isSpaceChar) {
                    word += 1
                }
                currentIndex += 1
            }

            buffer(currentIndex + (cipher.column - 1))
        }

        protected def isMatchingIndex(letter: Char, index: Int): Boolean = letter equals buffer(index)

        protected def randomIndex: Int = randomGenerator.nextInt(buffer.length)
    }

}