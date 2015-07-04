package bookcipher

import scala.util.Random

object Cipher {
    def fromLine(line: String): Cipher = {
        val params = line.split("-").map(_.toInt)
        Cipher(params(0), params(1), params(2))
    }
}

case class Cipher(line: Int, word: Int, column: Int) {
    override def toString() = f"$line%d-$word%d-$column%d%n"
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
        var currentChar = '\u0000'

        for (currentIndex <- 0 until codedIndex) {
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
        }

        Cipher(line, word, column)
    }

    def decode(cipher: Cipher): Char = {
        var line = 1
        var currentIndex = seekToIndex(0, cipher.line, _.equals('\n'))
        currentIndex = seekToIndex(currentIndex, cipher.word, _.isSpaceChar)
        currentIndex += (cipher.column - 1)
        buffer(currentIndex)
    }

    protected def seekToIndex(startIndex: Int, maxIterations: Int, isMatch: Char => Boolean): Int = {
        var iteration = 1
        var nextIndex = startIndex
        while (iteration != maxIterations) {
            if (isMatch(buffer(nextIndex))) {
                iteration += 1
            }
            nextIndex += 1
        }
        nextIndex
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