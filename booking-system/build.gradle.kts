plugins {
    application
}

application {
    mainClass.set("com.saurav.bookingsystem.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
