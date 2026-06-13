plugins {
    application
}

application {
    mainClass.set("com.saurav.conferenceroom.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
