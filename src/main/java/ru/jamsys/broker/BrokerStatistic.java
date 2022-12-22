package ru.jamsys.broker;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BrokerStatistic {

    long timestamp = System.currentTimeMillis();

    List<BrokerQueueStatistic> list = new ArrayList<>();

}
