# MpcHcRemote

A remote control app for MPC-HC (a media player for Windows). Allows to operate the player with an Android smartphone.

Features include:
* highly customizable - buttons can be added / deleted / moved using drag & drop technique and their corresponding commands can be reassigned with a couple of taps.
* shows status of currently played file - position, volume, title, thumbnail, etc
* remote file explorer - allows to browse and load files into the player remotely.
* server scanner - searches for available servers in the local network
* widget - allows to use most common functions even with a locked screen, using Android notification mechanism.


Overall architecture inspired by Android Clean Architecture: https://github.com/android10/Android-CleanArchitecture

External libs & technologies used:

* Dagger2 for dependency injection.
* Butterknife for view binding.
* RxJava for async execution & observer pattern impl.
* jsoup for http connections and html parsing.

How it looks: http://imgur.com/a/nLD1M

How it works (video): https://drive.google.com/open?id=0B2PXCL87K_5JdU5jQ1BSWVZTXzg
