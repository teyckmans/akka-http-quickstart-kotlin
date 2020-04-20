plugins {
    id("java")
    id("idea")
    id("application")
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.typesafe.akka:akka-http_2.13:10.1.11")
    implementation("com.typesafe.akka:akka-http-jackson_2.13:10.1.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.10.+")
    implementation("com.typesafe.akka:akka-actor-typed_2.13:2.6.4")
    implementation("com.typesafe.akka:akka-stream_2.13:2.6.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("com.typesafe.akka:akka-http-testkit_2.13:10.1.11")
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:2.6.4")
    testImplementation("junit:junit:4.12")
}

application {
    mainClassName = "com.example.QuickstartAppKt"
}

val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

