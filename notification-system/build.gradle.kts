plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.notificationsystem.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
