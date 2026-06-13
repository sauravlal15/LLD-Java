plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.mockinterview2.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
