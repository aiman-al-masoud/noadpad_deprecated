# noadpad
No-Ad-Pad is a simple, add-free, notes app for Android. In addition to the core note-taking functionalities, 
I've decided to include some fairly uncommon features for an Android notes app, including:

- self organizing collections: you can create collections add add a list of tags to each.
 The app will automatically parse your note files' text for tags, adding each note 
 file to an appropriate user-defined collection.
 
- script-like Java interpreter: this app makes use of the Beanshell Java Interpreter. This turns 
it into an appealing tool for quick back-of-the-envelope scripting, for example: 
to analyze a string of text in your notes  on the run, or to perform some quick 
calculation with Java's rich Math "library". (Beanshell's repo and license: https://github.com/beanshell/beanshell/blob/master/LICENSE).

- note-compacter feature: allows you to easily merge many smaller note files into a single big one. It can be useful
for unifying scattered pieces of a larger work, once you realize they should all stay together.

- download text from a webpage: this feature is still experimental. It makes use of the Jsoup library to
download (for now just the text) of a webpage, given a full link to it. (Jsoup's repo and license: https://github.com/jhy/jsoup/blob/master/LICENSE).
