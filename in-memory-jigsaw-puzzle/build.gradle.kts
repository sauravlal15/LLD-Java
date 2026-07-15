plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.inmemoryjigsawpuzzle.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
