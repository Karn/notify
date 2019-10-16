## Advanced Usage
> **Note:** This page is still a work-in-progress. You can help complete the documentation by contributing to the project.

#### NOTIFICATION ANATOMY

![Anatomy](./assets/anatomy.svg)

| ID   | Name         | Description                                                                                             |
| --- | --- | --- |
| 1    | Icon         | Set using the `Header#icon` field.                                                                      |
| 2    | App Name     | Application name, immutable.                                                                            |
| 3    | Header Text  | Optional description text. Set using the `Header#headerText` field.                                     |
| 4    | Timestamp    | Timestamp of the notification.                                                                          |
| 5    | Expand Icon  | Indicates that the notification is expandable.                                                          |
| 6    | Content      | The "meat" of the notification set using of of the `NotifyCreator#as[Type]((Type) -> Unit)` scoped functions. |
| 7    | Actions      | Set using the `NotifyCreator#actions((ArrayList<Action>) -> Unit)` scoped function.                           |

#### RESPONDING TO CLICKS

The `Payload.Meta` object provides `clickIntent` and `clearIntent` members which when not `null`, will be fired when clicked or dismissed.

```Kotlin
Notify
    .with(context)
    .meta { // this: Payload.Meta
        // Launch the MainActivity once the notification is clicked.
        clickIntent = PendingIntent.getActivity(context,
                                                  0,
                                                  Intent(context, MainActivity::class.java),
                                                  0)
        // Start a service which clears the badge count once the notification is dismissed.
        clearIntent = PendingIntent.getService(context,
                                                0,
                                                Intent(context, MyNotificationService::class.java)
                                                        .putExtra("action", "clear_badges"),
                                                0)
    }
    .content { // this: Payload.Content.Default
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    }
    .show()
```


#### ACTIONS

Similarly, we can construct actions which can be used to quickly perform tasks without opening your app. The actions are the collection of buttons that are shown below the notification to quickly perform tasks.

```Kotlin
Notify
    .with(context)
    .content { // this: Payload.Content.Default
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    }
    .actions { // this: ArrayList<Action>
        add(Action(
                // The icon corresponding to the action.
                R.drawable.ic_app_icon,
                // The text corresponding to the action -- this is what shows below the
                // notification.
                "Clear",
                // Swap this PendingIntent for whatever Intent is to be processed when the action
                // is clicked.
                PendingIntent.getService(context,
                        0,
                        Intent(context, MyNotificationService::class.java)
                                .putExtra("action", "clear_badges"),
                        0)
                ))
    }
    .show()
```


#### STACKABLE NOTIFICATIONS

Notify provides a solution to the idea of grouping notifications into a "stack". I.e the notifications are grouped into a single notification when there is more than one of the same type as defined by `stackable.key`.
This is a particularly effective method of reducing the clutter of the notification tray while also providing the relevant information.

```Kotlin
Notify
        .with(context)
        .content { // this: Payload.Content.Default
            title = "New dessert menu"
            text = "The Cheesecake Factory has a new dessert for you to try!"
        }
        // Define the notification as being stackable. This block should be the same for all
        // notifications which are to be grouped together.
        .stackable { // this: Payload.Stackable
            // In particular, this key should be the same. The properties of this stackable
            // notification as taken from the latest stackable notification's stackable block.
            key = "test_key"
            // This is the summary of this notification as it appears when it is as part of a
            // stacked notification. This String value is what is shown as a single line in the
            // stacked notification.
            summaryContent = "test summary content"
            // The number of notifications with the same key is passed as the 'count' argument. We
            // happen not to use it, but it is there if needed.
            summaryTitle = { count -> "Summary title" }
            // ... here as well, but we instead choose to use to to update the summary for when the
            // notification is collapsed.
            summaryDescription = { count -> count.toString() + " new notifications." }
        }
        .show()
```


#### BUBBLE NOTIFICATIONS

With the release of Android 10, Notify now also supports [Notification Bubbles](https://developer.android.com/guide/topics/ui/bubbles) on devices with the `Notification Bubbles` enabled through the Developer Settings. This new form of notification allows an application to display rich content from an activity at a glance to a user.

Begin by first creating an activity and adding the following permissions to that activity within your `AndroidManifest.xml`:

```xml
<application
        ...>

    <!-- Find the Activity being used for the Bubble Notification -->
    <activity
        ...
        <!-- Add the three lines below to the activity being used -->
        android:allowEmbedded="true"
        android:documentLaunchMode="always"
        android:resizeableActivity="true" />
</application>
```

Then you can target that activity from the notification.

```kotlin
Notify.with(context)
    .content { // this: Payload.Content.Default
        title = "New dessert menu"
        text = "The Cheesecake Factory has a new dessert for you to try!"
    }
    // Define the Notification as supporting a Bubble format. This style can be applied to any
    // notification.
    .bubblize { // this: Payload.Bubble
        // Configure the target Intent for the Notification to launch when it is expanded.
        val target = Intent(context, BubbleActivity::class.java)
        // Provide a PendingIntent to launch the above target once the Bubble is expanded.
        val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0)

        // Set the image for the Bubble, this uses the IconCompat class to build the icon being
        // shown within the bubble.
        bubbleIcon = IconCompat.createWithResource(context, R.drawable.ic_app_icon)
        // Set the activity that is being shown when the Bubble is expanded to the PendingIntent
        // created above.
        targetActivity = bubbleIntent
    }
    .show()
```
