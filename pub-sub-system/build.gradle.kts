plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.pubsubsystem.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
