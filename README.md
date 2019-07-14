KafkaLikeEventDispatecher [![](https://jitpack.io/v/Willena/KafkaLikeEventDispatcher.svg)](https://jitpack.io/#Willena/KafkaLikeEventDispatcher)
=========================

This project is one more event dispatcher for java applications. This event dispatcher is built on the same principle 
of Apache Kafka.
But with a more limited set of features.

Here is what you can do:
* Read from start of the log.
* Write the the log.
* Have multiple independent "clients".
* Have Local and Remote "client".
* Each client is independant.
* Can produce and consume any Serialisable objects.
* Internal TCP client and Server for remote clients.
* Topic management. 


A lot of things are missing, but the idea of this project is not to make a full replica of Kafka.
Only something that uses the same concepts of a log writable that keep the order of element pushed to it and deliver 
them in the same order

## Usage Example

You can have a look at the three example available in the fr.guillaumevillena.KafkaLikeEventDispatcher.example package.

Here is a Quick example

```java

 public static void main(String[] args) {

    //Initialize a local client
    LocalKafkaLikeClient client = new LocalKafkaLikeClient();

    // Register the client on two topics (MAIN, SEC)
    client.register(new String[]{"MAIN", "SEC"});
    
    // When a new even is available the callback is called
    // Dont forget to commit if you want to have the next event. /!\ 
    client.addEventCallback(new KafkaLikeMultipleTopicEventListenner() {
      @Override
      public void onEventReceived(Object data, String topic) {
        System.out.println("CLIENT : topic = " + topic + " data " + data);
        client.commit(topic);
      }
    });

    //Produce 2 events one the SEC topic and one on MAIN
    client.produceEvent("LEvent7", "SEC");
    client.produceEvent("LEvent8", "MAIN");

    //The Event loop need to be triggered in order to receive event and callbacks
    //Might need to be exported to a thread.
    while (true) {
      client.askForEvent();
    }


```

## Issues and Pull requests 

If you find issues or are willing to add new functionalities to this project do not hesitate to create an issue or a pull request !

## Licence 

```
The MIT License

Copyright (c) 2018- Guillaume VILLENA, https://github.com/willena

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
