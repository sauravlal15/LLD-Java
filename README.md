# javaPractice

Gradle multi-project repo for **low-level design (LLD)** practice. Each problem is its own subproject with a separate classpath and build output, so types like `Payment` or `Main` in one problem never conflict with another.

## Problems

| Folder | Run |
|--------|-----|
| `parking-lot/` | `./gradlew :parking-lot:run` |
| `booking-system/` | `./gradlew :booking-system:run` |

## Add a new problem

```bash
./scripts/new-problem.sh elevator-system
./gradlew :elevator-system:run
```

If `settings.gradle.kts` was not updated automatically, add `"elevator-system",` to the `include(...)` block.

### Conventions

- **Folder name:** kebab-case (`parking-lot`, `elevator-system`)
- **Package:** `com.saurav.lld.<slugWithoutHyphens>` for new problems (e.g. `com.saurav.lld.elevatorsystem`)
- **Layout:** `src/main/java/<package>/...` — never use the default package
- **One problem = one Gradle subproject** — do not share source roots across problems

Older problems (`parking-lot`, `booking-system`) keep their original packages (`com.saurav.parkinglot`, `com.saurav.bookingsystem`).

## IDE setup

**IntelliJ / Cursor:** open the **repo root** (`javaPractice`) and import as a **Gradle** project. Do not mark multiple problem folders as one combined source root.

**VS Code:** Java extension will pick up each subproject from Gradle.

## Build all problems

```bash
./gradlew build
```

Compile or run a single problem:

```bash
./gradlew :parking-lot:compileJava
./gradlew :booking-system:run
```

## Repo layout

```
javaPractice/
├── settings.gradle.kts      # lists all problem subprojects
├── build.gradle.kts         # shared Java toolchain
├── scripts/new-problem.sh   # scaffold a new problem
├── templates/lld-problem/   # template for new-problem.sh
├── parking-lot/
│   ├── build.gradle.kts
│   └── src/main/java/...
└── booking-system/
    └── ...
```
