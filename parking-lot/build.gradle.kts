plugins {
    application
}

application {
    mainClass.set("com.saurav.parkinglot.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
