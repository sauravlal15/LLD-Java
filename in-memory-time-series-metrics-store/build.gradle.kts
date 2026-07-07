plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.inmemorytimeseriesmetricsstore.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
