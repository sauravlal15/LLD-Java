plugins {
    application
}

application {
    mainClass.set("com.saurav.lld.dsapractice.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
