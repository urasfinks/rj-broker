package ru.jamsys;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import ru.jamsys.component.Broker;
import ru.jamsys.component.BrokerStatisticReader;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @BeforeAll
    static void beforeAll() {
        String[] args = new String[]{};
        App.context = SpringApplication.run(App.class, args);
    }

    @Test
    void main() {
        Broker bean = App.context.getBean(Broker.class);
        BrokerStatisticReader bean2 = App.context.getBean(BrokerStatisticReader.class);
        Util.sleepMillis(5000);
    }
}