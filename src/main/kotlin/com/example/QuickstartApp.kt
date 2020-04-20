package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Adapter
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.http.javadsl.server.Route
import akka.stream.Materializer
import java.time.Duration
import java.util.concurrent.TimeUnit

fun main() {
    val rootBehavior = Behaviors.setup { context: ActorContext<Void> ->
        val userRegistryActor = context.spawn(UserRegistry.create(), "UserRegistry")
        val userRoutes = UserRoutes(context.system, userRegistryActor)
        startHttpServer(userRoutes.userRoutes(), context.system)
        Behaviors.empty()
    }

    // boot up server using the route as defined below
    ActorSystem.create(rootBehavior, "HelloAkkaHttpServer")


}

fun startHttpServer(route: Route, system: ActorSystem<*>) {
    // Akka HTTP still needs a classic ActorSystem to start
    val classicSystem = Adapter.toClassic(system)
    val http = Http.get(classicSystem)
    val materializer = Materializer.matFromSystem(system)
    val routeFlow = route.flow(classicSystem, materializer)
    val futureBinding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8080), materializer)

    futureBinding.toCompletableFuture().get(3, TimeUnit.SECONDS)

    futureBinding.whenComplete { binding: ServerBinding?, exception: Throwable? ->
        if (binding != null) {
            val address = binding.localAddress()
            system.log().info("Server online at http://{}:{}/",
                    address.hostString,
                    address.port)

            Runtime.getRuntime().addShutdownHook(Thread {
                system.log().info("Stopping in 3-2-1...")
                binding.terminate(Duration.ofSeconds(3)).toCompletableFuture()
                        .thenAccept {
                            system.terminate()
                        }
                println("Elvis has left the building...")
            })

        } else {
            system.log().error("Failed to bind HTTP endpoint, terminating system", exception)
            system.terminate()
        }
    }
}