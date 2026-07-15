plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.browserhistory.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
