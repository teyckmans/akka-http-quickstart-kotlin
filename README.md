# akka-http-quickstart-kotlin

I have finally got around to start experimenting with Akka-http. 
While doing my initial experiments I thought I try to convert the quickstart to Kotlin and see what that would look like.
This is the result. I kinda like it.

Converted the [akka-http-quickstart-http](https://developer.lightbend.com/guides/akka-http-quickstart-java/) to Kotlin.
Including the Gradle build.

## Additions

Shutdown hook for gracefully stopping the server.

Jackson object-mapper additionally configured to find and register modules.
Java 8 and Kotlin modules have been added to the dependencies.

## TODO
RejectEmptyResponse does not work properly. 
Find a general way to reply with StatusCodes.NOT_FOUND in case an Optional is empty. 