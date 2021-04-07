# noadpad
No-Ad-Pad is a simple, ad-free (as the name says), notes app for Android. In addition to the core note-taking functionalities, 
I've decided to include some fairly uncommon features for an Android notes app, including:

- self organizing collections: you can create collections of note files, and each collection can have a list of tags.
 If the option is enabled, the app will automatically parse your note files' text for tags, adding each note 
 file to an appropriate user-defined collection. For example: if you want to make sure that every note file 
 with the words: "github" or "java" appear in one place, you can create a collection, say "projects", and add to it the tags 
 "github" and "java".
 
- script-like Java interpreter: this app makes use of the Beanshell Java Interpreter. This turns 
it into an appealing tool for quick back-of-the-envelope scripting, for example: 
to analyze a string of text in your notes  on the run, or to perform some quick 
calculation with Java's rich Math "library". (Beanshell's repo and license: https://github.com/beanshell/beanshell/blob/master/LICENSE).

- note-compacter feature: allows you to easily merge many smaller note files into a single big one. It can be useful
for unifying scattered pieces of a larger work, once you realize they should all stay together.

- encryption: it is possible to encrypt a note file with a user-provided key, as well as a (less convenient but more secure) auto-generated OTP, that is as long 
as the encrypted text. It is also possible to encrypt multiple files with the same key (in this case the OTP option isn't provided, for obvious reasons). The keys aren't stored in memory, so any attempt at decryption should be handled with care, as the app will run no checks on the validity of the decryption key provided
by the user. The encryption algorithm used is a simple implementation of the Vigenere cipher: https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher.

- download text from a webpage: this feature is still experimental. It makes use of the Jsoup library to
download (for now just the text) of a webpage, given a full link to it. (Jsoup's repo and license: https://github.com/jhy/jsoup/blob/master/LICENSE).


Some of the more common features include:

- global search by keywords.
- option to adjust the text size.
- note files get automatically sorted by their date last edited.

