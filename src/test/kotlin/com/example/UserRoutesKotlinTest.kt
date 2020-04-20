package com.example

import akka.actor.testkit.typed.javadsl.TestKitJunitResource
import akka.actor.typed.ActorRef
import akka.http.javadsl.model.HttpRequest
import akka.http.javadsl.model.MediaTypes
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.testkit.JUnitRouteTest
import akka.http.javadsl.testkit.TestRoute
import org.junit.*
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UserRoutesKotlinTest : JUnitRouteTest() {

    companion object {
        @JvmField
        @ClassRule
        val testkit = TestKitJunitResource()

        @JvmStatic
        lateinit var userRegistry: ActorRef<UserRegistry.Command>

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            userRegistry = testkit.spawn(UserRegistry.create())
        }

    }

    private lateinit var appRoute: TestRoute

    @Before
    fun before() {
        val userRoutes = UserRoutes(testkit.system(), userRegistry)
        appRoute = testRoute(userRoutes.userRoutes())
    }

    @Test
    fun test1NoUsers() {
        appRoute.run(HttpRequest.GET("/users"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"users\":[]}")
    }

    @Test
    fun test2HandlePOST() {
        appRoute.run(HttpRequest.POST("/users")
                .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                        "{\"name\": \"Kapi\", \"age\": 42, \"countryOfResidence\": \"jp\"}"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertMediaType("application/json")
                .assertEntity("{\"description\":\"User Kapi created.\"}")
    }

    @Test
    fun test3Remove() {
        appRoute.run(HttpRequest.DELETE("/users/Kapi"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"description\":\"User Kapi deleted.\"}")
    }
}