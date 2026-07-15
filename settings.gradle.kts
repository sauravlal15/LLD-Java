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
    "mock-interview-3",
    "notification-system",
    "coupon-management-system",
    "vending-machine",
    "in-memory-time-series-metrics-store",
    "browser-history",
    "hashmap",
    "in-memory-jigsaw-puzzle",
    "dsa-practice",
)
