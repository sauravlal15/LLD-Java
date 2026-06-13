plugins {
    application
}

application {
    mainClass.set("com.saurav.ratelimitter.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
