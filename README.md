![Notify](./docs/assets/notify-logo.svg)


## Notify
Simplified notification construction for Android.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.11-blue.svg?style=flat-square)](http://kotlinlang.org)
[![AndroidX](https://img.shields.io/badge/AndroidX-1.0-6ab344.svg?style=flat-square)](https://developer.android.com/jetpack/androidx/)
[![Build Status](https://img.shields.io/travis/Karn/notify.svg?style=flat-square)](https://travis-ci.org/Karn/notify)
[![Codecov](https://img.shields.io/codecov/c/github/karn/notify.svg?style=flat-square)](https://codecov.io/gh/Karn/notify)
[![GitHub (pre-)release](https://img.shields.io/github/release/karn/notify/all.svg?style=flat-square)
](./../../releases)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FKarn%2Fnotify.svg?type=small)](https://app.fossa.io/projects/git%2Bgithub.com%2FKarn%2Fnotify?ref=badge_small)

Notify is a Fluent API for Android notifications which lets you build notifications without worrying how they'll look across devices or API versions. You can bring deterministic notifications to your Android projects with clarity & ease so you can finally stop fighting Developer documentation and get back to dev work that really matters.

#### GETTING STARTED
Notify (pre-)releases are available via JitPack. It is recommended that  a specific release version is selected when using the library in production as there may be breaking changes at anytime.

> **Tip:** Test out the canary channel to try out features by using the latest develop snapshot; `develop-SNAPSHOT`.

```Groovy
// Project level build.gradle
// ...
repositories {
    maven { url 'https://jitpack.io' }
}
// ...

// Module level build.gradle
dependencies {
    // Replace version with release version, e.g. 1.0.0-alpha, -SNAPSHOT
    implementation "io.karn:notify:[VERSION]"
}
```


#### USAGE
The most basic case is as follows:

```Kotlin
Notify
    .with(context)
    .content { // this: Payload.Content.Default
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    }
    .show()
```

![Basic usecase](./docs/assets/default.svg)

If you run into a case in which the library does not provide the requisite builder functions you can get the `NotificationCompat.Builder` object and continue to use it as you would normally by calling `NotifyCreator#asBuilder()`.

> **Tip:** You can view other notification styles on the [Notification Types](./docs/types.md) docs page.

> **Tip:** Advanced usage topics are documented [here](./docs/advanced.md).


#### CONTRIBUTING
There are many ways to [contribute](./.github/CONTRIBUTING.md), you can
- submit bugs,
- help track issues,
- review code changes.
