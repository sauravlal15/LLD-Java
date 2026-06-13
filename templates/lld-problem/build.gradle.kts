plugins {
    application
}

application {
    mainClass.set("{{MAIN_CLASS}}")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
