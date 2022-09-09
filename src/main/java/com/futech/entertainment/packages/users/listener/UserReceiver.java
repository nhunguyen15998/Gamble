// package com.futech.entertainment.packages.users.listener;

// import java.util.concurrent.CountDownLatch;

// import org.springframework.stereotype.Component;

// @Component
// public class UserReceiver {
//     private CountDownLatch latch = new CountDownLatch(1);

//     public void receiveMessage(String message) {
//         System.out.println("Received <" + message + ">");
//         latch.countDown();
//     }

//     public CountDownLatch getLatch() {
//         return latch;
//     }
// }
