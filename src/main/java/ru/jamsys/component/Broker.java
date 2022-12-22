package ru.jamsys.component;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.jamsys.AbstractCoreComponent;
import ru.jamsys.App;
import ru.jamsys.broker.BrokerQueue;
import ru.jamsys.broker.BrokerStatistic;
import ru.jamsys.scheduler.SchedulerGlobal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Lazy
public class Broker extends AbstractCoreComponent {

    private final Map<Class<?>, BrokerQueue<?>> mapQueue = new ConcurrentHashMap<>();
    private final Scheduler schedulerGlobalStatistic;

    public Broker(Scheduler schedulerGlobalStatistic) {
        this.schedulerGlobalStatistic = schedulerGlobalStatistic;
        schedulerGlobalStatistic.add(SchedulerGlobal.SCHEDULER_GLOBAL_STATISTIC_WRITE, this::flushStatistic);
    }

    @SuppressWarnings({"unchecked"})
    private <T> BrokerQueue<T> get(Class<T> c) {
        if (!mapQueue.containsKey(c)) {
            mapQueue.put(c, new BrokerQueue<T>());
        }
        return (BrokerQueue<T>) mapQueue.get(c);
    }

    @SuppressWarnings("unused")
    public <T> void setLimit(Class<T> c, int limit) {
        BrokerQueue<T> brokerQueue = get(c);
        brokerQueue.setLimit(limit);
    }

    @SuppressWarnings("unused")
    public <T> void addElement(Class<T> c, T o) {
        BrokerQueue<T> brokerQueue = get(c);
        brokerQueue.add(o);
    }

    @SuppressWarnings("unused")
    public <T> T getElement(Class<T> c, boolean last) {
        BrokerQueue<T> tBrokerQueue = get(c);
        return tBrokerQueue.get(last);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        schedulerGlobalStatistic.remove(SchedulerGlobal.SCHEDULER_GLOBAL_STATISTIC_WRITE, this::flushStatistic);
        mapQueue.clear();
    }

    @Override
    public void flushStatistic() {
        Class<?>[] objects = mapQueue.keySet().toArray(new Class[0]);
        if (objects.length > 0) {
            BrokerStatistic brokerStatistic = new BrokerStatistic();
            for (Class<?> key : objects) {
                BrokerQueue<?> brokerQueue = mapQueue.get(key);
                brokerStatistic.getList().add(brokerQueue.flushStatistic());
            }
            StatisticAggregator statistic = App.context.getBean(StatisticAggregator.class);
            statistic.add(brokerStatistic);
        }
    }

}
