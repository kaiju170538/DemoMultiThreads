package com.example.multithreads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivityExecutor extends AppCompatActivity {
    private TextView statusTextView;
    private Button startButton;
    private ExecutorService executorService;
    private AtomicInteger isComplete = new AtomicInteger(0); // Use AtomicInteger for thread-safe counting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        executorService = Executors.newFixedThreadPool(3); // 3 threads for 3 tasks

        startButton.setOnClickListener(v -> processTasksWithExecutor());
    }

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

    // Simulate a task with a given delay
    private void simulateTasks(String orderDetails, int delay) {
        try {
            Thread.sleep(delay); // Simulate task delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Check if all tasks are done
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down executor to prevent memory leaks
    }
}
