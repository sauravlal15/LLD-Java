plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.mockinterview3.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
