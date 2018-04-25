![Notify](./docs/assets/notify-logo.svg)


## Notify
[![Kotlin](https://img.shields.io/badge/Kotlin-1.2.21-blue.svg?style=flat-square)](http://kotlinlang.org)
[![RxJava](https://img.shields.io/badge/Support-27.1.0-6ab344.svg?style=flat-square)](https://github.com/ReactiveX/RxJava/releases/tag/v2.1.10)
[![Build Status](https://img.shields.io/travis/Karn/notify.svg?style=flat-square)](https://travis-ci.org/Karn/notify)
[![GitHub release](https://img.shields.io/github/release/karn/notify-lightgrey.svg?style=flat-square)](/releases/latest)



Clean system notifications for Android.

###### GETTING STARTED
You can install Notify using Jitpack while it is still in development.

As such there currently is only a snapshot available until test coverage is improved.

``` Groovy
// Project level build.gradle
// ...
repositories {
    maven { url 'https://jitpack.io' }
}
// ...

// Module level build.gradle
dependencies {
    implementation "com.github.karn:notify:-SNAPSHOT"
}
```

###### USAGE
The most basic case is as follows:

``` kotlin
Notify
    .with(context)
    .content(
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    )
    .send()
```

![Basic usecase](./docs/assets/default.svg)



###### CONTRIBUTING
There are many ways to [contribute](./.github/CONTRIBUTING.md), you can
- submit bugs,
- help track issues,
- review code changes.