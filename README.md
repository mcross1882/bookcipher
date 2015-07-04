bookcipher
==========

A simple utility -- let very limited -- utility for creating book ciphers. The application
works by taking a source document and matching the characters in it against another document.
The codes are then outputted to a separate file which can be reversed later.

### Installing & Running

To run the program run the following command (requires `sbt` be installed on your machine)

```
$ sbt run
```

### How does it work?

The application works by iterating over every character in the source document. For every character it will 
find a matching character -- via random selection -- in the cipher file. Once it finds a match it will encode
the line number, word position on the line and the column within the word that the given letter maps to.

For instance given the example below

*cipher.txt*
```
The blue whale
The brown dog
The gray cat
```

*source.txt*
```
do well
```

If we examine the character `w` in `well` we note that it only appears once in the cipher. Therefore it will always map to the `w` in `whale`
when using that particular cipher file. That means the encoding for `w` would be `1-3-1`. This brings up a caveat in the system,
if your cipher does not contain all the characters in your source file it cannot be used to encrypt it. The program will throw an
error if this happens letting you know which character is missing.
