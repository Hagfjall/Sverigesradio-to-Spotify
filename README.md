Sverigesradio-to-Spotify
========================

This project are creating a playlist from songs played on a specified program and day on Sverigesradio.se. My first JAVA EE program and my first interactions with Spotifys API, have that in mind while reading the code. ;) Used Tomcat 8 during my development. 

Beta, doesn't handle errors very good due to my lack of web programming. 
The only error that the user get to know is if the user input has any errors in form of unparsable date or if it's a valid URL or not. Nothing about the connection to Spotify's servers or other errors. 

The server starts to crawl the page at sverigesradio.se and getting the songs right after the user has got the login link to Spotify. This means that if the user is faster than the crawler it will either get a error message saying there's no cache or it will wait until the writing of the cache is done. 