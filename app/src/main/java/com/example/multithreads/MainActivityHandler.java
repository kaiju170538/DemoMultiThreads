package com.example.multithreads;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivityHandler extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> startMultipleOrders());
    }

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

    // Simulate processing an order with a delay
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

    // Reset UI and re-enable the button for a new round of orders
    private void resetUI() {
        handler.postDelayed(() -> {
            statusTextView.setText("All orders completed! Ready to start again.");
            startButton.setEnabled(true); // Re-enable the button
        }, 1000); // Delay for a second before resetting
    }
}