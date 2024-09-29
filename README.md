# DemoMultiThreads
## activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">

    <TextView
        android:id="@+id/statusTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Ready to process multiple orders!"
        android:textSize="18sp"
        android:layout_gravity="center"/>

    <Button
        android:id="@+id/startButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Start Cooking"
        android:layout_gravity="center"
        android:layout_marginTop="20dp"/>
</LinearLayout>
```
## MainActivityHandler.java
### Declare component
```java
    private TextView statusTextView;
    private Button startButton;
    private Handler handler = new Handler();

    // Steps for multiple orders
    private String[] orders = {
            "Order 1: Preparing Salad",
            "Order 2: Baking Pizza",
            "Order 3: Grilling Burger"
    };

    // Atomic counter to track completed threads
    private AtomicInteger completedOrders = new AtomicInteger(0);
```
### onCreate(Bundle savedInstanceState) method
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> startMultipleOrders());
    }
```
### startMultipleOrders() method
```java
    private void startMultipleOrders() {
        statusTextView.setText("Processing multiple orders...");
        startButton.setEnabled(false); // Disable the button during processing

        // Reset the counter at the start
        completedOrders.set(0);

        // Create separate threads for each order
        Thread order1 = new Thread(() -> processOrder("Order 1: Preparing Salad", 3000));
        Thread order2 = new Thread(() -> processOrder("Order 2: Baking Pizza", 5000));
        Thread order3 = new Thread(() -> processOrder("Order 3: Grilling Burger", 4000));

        // Start all orders simultaneously
        order1.start();
        order2.start();
        order3.start();
    }
```
### processOrder(String orderDetails, int delay) method
```java
    private void processOrder(String orderDetails, int delay) {
        try {
            Thread.sleep(delay); // Simulate cooking time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Update the UI after the order is done
        handler.post(() -> {
            statusTextView.setText(orderDetails + " - Done!");

            // Increment the count of completed orders
            int completed = completedOrders.incrementAndGet();

            // If all orders are done, reset the UI and enable the button
            if (completed == orders.length) {
                resetUI();
            }
        });
    }
```
### resetUI() method
```java
    private void resetUI() {
        handler.postDelayed(() -> {
            statusTextView.setText("All orders completed! Ready to start again.");
            startButton.setEnabled(true); // Re-enable the button
        }, 1000); // Delay for a second before resetting
    }
```

## MainActivityRunOnUiThread.java
### Declare component
```java
    private TextView statusTextView;
    private Button startButton;
    private AtomicInteger isCompleted = new AtomicInteger(0);
```
### onCreate(Bundle savedInstanceState) method
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> processActWithRunOnUiThread());
    }
```
### processActWithRunOnUiThread() method
```java
private void processActWithRunOnUiThread() {
        statusTextView.setText("Processing to planning an Event...");
        startButton.setEnabled(false);
        isCompleted.set(0); // Reset completed count

        // Thread for Act 1
        new Thread(() -> {
            simulateAct("Activity 1: Sending invitations", 3000);
            runOnUiThread(() -> {
                statusTextView.setText("Activity 1: Sending invitations - Completed!");
                checkActivityCompleted();
            });
        }).start();

        // Thread for Act 2
        new Thread(() -> {
            simulateAct("Activity 2: Ordering food", 5000);
            runOnUiThread(() -> {
                statusTextView.setText("Activity 2: Ordering food - Completed!");
                checkActivityCompleted();
            });
        }).start();

        // Thread for Act 2
        /*new Thread(() -> {
            simulateOrder("Activity 2: Ordering food", 5000);
            runOnUiThread(() -> statusTextView.setText("Activity 2: Ordering food - Completed!");
            checkActivityCompleted();
        }).start();*/

        // Thread for Order 3
        new Thread(() -> {
            simulateAct("Activity 3: Decorating", 7000);
            runOnUiThread(() -> {
                statusTextView.setText("Activity 3: Decorating - Completed!");
                checkActivityCompleted();
            });
        }).start();
    }
```
### simulateAct(String orderDetails, int delay) method
```java
    private void simulateAct(String orderDetails, int delay) {
        try {
            Thread.sleep(delay); // Simulate cooking time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```
### checkActivityCompleted() method
```java
    private void checkActivityCompleted() {
        if (isCompleted.incrementAndGet() == 3) {
            new Thread(() -> {
                try {
                    Thread.sleep(0); // Delay for 2 seconds to view Activity 3 completion
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    statusTextView.setText("All activities are completed!");
                    startButton.setEnabled(true); // Re-enable button
                });
            }).start();
        }
    }
```
## MainActivityExecutor.java
### Declare component
```java
    private TextView statusTextView;
    private Button startButton;
    private ExecutorService executorService;
    private AtomicInteger isComplete = new AtomicInteger(0); // Use AtomicInteger for thread-safe counting
```
### onCreate(Bundle savedInstanceState) method
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        executorService = Executors.newFixedThreadPool(3); // 3 threads for 3 tasks

        startButton.setOnClickListener(v -> processTasksWithExecutor());
    }
```
### processTasksWithExecutor() method
```java
    private void processTasksWithExecutor() {
        statusTextView.setText("Processing running tasks...");
        startButton.setEnabled(false);
        isComplete.set(0); // Reset completed orders count

        // Submit Task 1 (Download file) to Executor
        executorService.execute(() -> {
            simulateTasks("Task 1: Download 15GB file", 10000);
            runOnUiThread(() -> {
                statusTextView.setText("Task 1: Download 15GB file - Completed!");
                checkIfAllTaskCompleted();
            });
        });

        // Submit Task 2 (Listening to a song) to Executor
        executorService.execute(() -> {
            simulateTasks("Task 2: Listening to a 5s Youtube song", 5000);
            runOnUiThread(() -> {
                statusTextView.setText("Task 2: Watching Youtube - Completed!");
                checkIfAllTaskCompleted();
            });
        });

        // Submit Task 3 (Playing a game) to Executor
        executorService.execute(() -> {
            simulateTasks("Task 3: Playing Game", 6000);
            runOnUiThread(() -> {
                statusTextView.setText("Task 3: Playing Game - Completed!");
                checkIfAllTaskCompleted();
            });
        });
    }
```
### simulateTasks(String orderDetails, int delay) method
```java
    // Simulate a task with a given delay
    private void simulateTasks(String orderDetails, int delay) {
        try {
            Thread.sleep(delay); // Simulate task delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```
### checkIfAllTaskCompleted() method
```java
    private void checkIfAllTaskCompleted() {
        if (isComplete.incrementAndGet() == 3) { // Safely increment and check
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // 2-second delay before showing the final message
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Post the final message back to the UI thread
                runOnUiThread(() -> {
                    statusTextView.setText("All running tasks are completed!");
                    startButton.setEnabled(true); // Re-enable button
                });
            }).start();
        }
    }
```
### onDestroy()
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down executor to prevent memory leaks
    }
```

## AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MultiThreads"
        tools:targetApi="31">

        <!-- Change the activity you want to launch here -->
        <!--<activity
            android:name=".MainActivityHandler"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>-->

        <!-- Uncomment this and comment the above one when testing runOnUiThread -->
        <activity
            android:name=".MainActivityRunOnUiThread"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Uncomment this and comment others when testing Executor -->
        <!--<activity
            android:name=".MainActivityExecutor"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>-->

    </application>

</manifest>
```
