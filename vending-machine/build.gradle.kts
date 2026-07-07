plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.vendingmachine.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
