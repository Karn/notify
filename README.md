![Notify](./docs/assets/notify-logo.svg)


## Notify
Simplified notification delivery for Android.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.2.21-blue.svg?style=flat-square)](http://kotlinlang.org)
[![RxJava](https://img.shields.io/badge/Support-27.1.0-6ab344.svg?style=flat-square)](https://github.com/ReactiveX/RxJava/releases/tag/v2.1.10)
[![Build Status](https://img.shields.io/travis/Karn/notify.svg?style=flat-square)](https://travis-ci.org/Karn/notify)
[![Codecov](https://img.shields.io/codecov/c/github/karn/notify/master.svg?style=flat-square)](https://codecov.io/gh/Karn/notify)
[![GitHub (pre-)release](https://img.shields.io/github/release/karn/notify/all.svg?style=flat-square)
](./../../releases)


###### GETTING STARTED
You can install Notify using Jitpack while it is still in development.

As such there currently are pre-releases available until test coverage is improved.

``` Groovy
// Project level build.gradle
// ...
repositories {
    maven { url 'https://jitpack.io' }
}
// ...

// Module level build.gradle
dependencies {
    // -SNAPSHOT (latest release)
    implementation "io.karn:notify:-SNAPSHOT"
}
```


###### USAGE
The most basic case is as follows:

``` kotlin
Notify
    .with(context)
    .content { // this: Payload.Content.Default
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    }
    .send()
```

![Basic usecase](./docs/assets/default.svg)


###### CONTRIBUTING
There are many ways to [contribute](./.github/CONTRIBUTING.md), you can
- submit bugs,
- help track issues,
- review code changes.