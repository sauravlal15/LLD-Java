plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.hashmap.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
