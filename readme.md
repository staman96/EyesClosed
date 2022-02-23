# Assignment in Software Development for Networks and Telecommications

#### Authors:

Skordoulis Dimitrios, Manolas Stamatis, Papaspyrou Ioannis, Loukakis Evaggelos

## About

It's a system that aims to avoid driving accidents by monitoring the driver's tiredness with an EEG device connected to a smartphone. 

### How the System works

The driver's smartphone periodically sends accelerometer, GPS and Electroencephalography (EEG) data, the latter being collected with the device "EMOTIV Epoc+", to an Edge server. The edge server passes the data to the backhaul server in order to be stored on an SQL database. The backhaul server sends a training model to the edge server in order for the data to be classified. Depending on the result, the edge server communicates with the smartphones if it is necessary to notify drivers of another driver being tired closeby or alert a driver that is tired.


The protocols used for the various communication stages are as seen in the picture below:

![image](https://user-images.githubusercontent.com/16540739/155374104-9e0b2a3b-c3c4-4a81-84dd-0ce83a47d103.png)

### Framworks used:
* maven (servers build)
* spring boot(websocket and STOMP)
* mosquito broker (MQTT)


The project was run on 2 android smartphones, and 2 windows laptps(on Intelij IDEA) on a local network with data that was already gathered.

