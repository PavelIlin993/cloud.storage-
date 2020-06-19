package com.pavelilin.cloud.storage.server;


import org.apache.log4j.BasicConfigurator;

public class MainServer {

  static {
    BasicConfigurator.configure();
  }

  public static void main(String[] args) {
    ServerFileSharingBehavior server = new SocketServerFileSharingBehavior();
    boolean connected = server.connect();
    if (connected) {
      server.listenToTheChannel();
    }
  }
}
