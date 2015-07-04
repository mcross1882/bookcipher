import java.io.PrintWriter
import scala.io.Source
import scala.util.Random

object Application {

    def main(args: Array[String]) {
        val encryptedStream = new PrintWriter("sample/encrypted.txt")
        val decryptedStream = new PrintWriter("sample/decrypted.txt")
        val source = Source.fromFile("sample/source.txt")
        val cipher = Source.fromFile("sample/cipher.txt").mkString
        val bookCipher = new BookCipher(cipher)

        for (letter <- source) {
            encryptedStream.write("%s%n".format(bookCipher.encode(letter).toString))
        }

        encryptedStream.flush
        encryptedStream.close

        Source.fromFile("sample/encrypted.txt").getLines.foreach { line =>
            decryptedStream.write(bookCipher.decode(Cipher.fromLine(line)))
        }

        decryptedStream.flush
        decryptedStream.close
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
            val codedIndex = findMatchingIndex(letter) match {
                case Some(value) => value
                case None => throw new Exception(s"Cipher does not contain the character $letter")
            }

            var line = 1
            var word = 1
            var column = 1
            var currentChar = '\0'
            var currentIndex = 0

            while (currentIndex != codedIndex) {
                currentChar = buffer(currentIndex)
                if ('\n' equals currentChar) {
                    line += 1
                    word = 1
                    column = 1
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

        protected def findMatchingIndex(letter: Char): Option[Int] = {
            if (!buffer.contains(letter)) {
                return None
            }

            var codedIndex = 0
            while (!isMatchingIndex(letter, codedIndex)) {
                codedIndex = randomIndex
            }
            Some(codedIndex)
        }

        protected def isMatchingIndex(letter: Char, index: Int): Boolean = letter equals buffer(index)

        protected def randomIndex: Int = randomGenerator.nextInt(buffer.length)
    }
}