rootProject.name = "javaPractice"

// Each LLD problem is an isolated Gradle subproject (own classpath + build output).
include(
    "parking-lot",
    "booking-system",
    "conference-room",
    "elevator-system",
    "ratelimitter",
    "pub-sub-system",
    "mock-interview-2",
)
