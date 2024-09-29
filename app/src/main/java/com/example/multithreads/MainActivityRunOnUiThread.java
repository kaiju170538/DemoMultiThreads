package com.example.multithreads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivityRunOnUiThread  extends AppCompatActivity {
    private TextView statusTextView;
    private Button startButton;
    private AtomicInteger isCompleted = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> processActWithRunOnUiThread());
    }

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

    // Simulate an order with a given delay
    private void simulateAct(String orderDetails, int delay) {
        try {
            Thread.sleep(delay); // Simulate cooking time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Check if all 3 orders are done
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

    // Check if all 3 orders are done
    /*private void checkActivityCompleted() {
        if (isCompleted.incrementAndGet() == 3) {
            runOnUiThread(() -> {
                statusTextView.setText("All activities are completed!");
                startButton.setEnabled(true); // Re-enable button
            });
        }
    }*/
}
