package bookcipher

import java.io.PrintWriter
import scala.io.Source

object Application {

    private val HelpMessage =
"""
Usage:
bookcipher [encrypt|decrypt] [cipher file] [source file] [output file]

encrypt/descrypt          Encrypt/descrypt the source file
cipher file               A file containing text that can be mapped to your source file
source file               The source file to encrypt or decrypt
output file               The output file to write to
"""

    def main(args: Array[String]) {
        args match {
            case Array("encrypt", cipherFile, sourceFile, outputFile) => encryptFile(cipherFile, sourceFile, outputFile)
            case Array("decrypt", cipherFile, sourceFile, outputFile) => decryptFile(cipherFile, sourceFile, outputFile)
            case _ => println(HelpMessage)
        }
    }

    protected def encryptFile(cipherFile: String, sourceFile: String, outputFile: String) {
        val source = Source.fromFile(sourceFile)
        val encryptedStream = new PrintWriter(outputFile)
        val cipher = Source.fromFile(cipherFile).mkString
        val bookCipher = new BookCipher(cipher)

        source.map(bookCipher.encode)
            .foreach(encryptedStream.print)

        encryptedStream.flush
        encryptedStream.close
    }

    protected def decryptFile(cipherFile: String, sourceFile: String, outputFile: String) {
        val decryptedStream = new PrintWriter(outputFile)
        val cipher = Source.fromFile(cipherFile).mkString
        val bookCipher = new BookCipher(cipher)

        Source.fromFile(sourceFile)
            .getLines
            .map(Cipher.fromLine)
            .map(bookCipher.decode)
            .foreach(decryptedStream.print)

        decryptedStream.flush
        decryptedStream.close
    }
}