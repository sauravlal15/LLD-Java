plugins {
    application
}

application {
    mainClass.set("com.saurav.elevatordesign.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
