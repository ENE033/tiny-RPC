package RPC.serviceImpl;

import RPC.core.annotation.RPCService;
import RPC.service.TestService;

@RPCService(beanName = "testService")
public class TestServiceImpl implements TestService {
    public static int count = 0;

    @Override
    public String getAnswer(String name) {
        return name.toLowerCase();
    }

    @Override
    public String getAnswer(String name, Integer age) {
        return name + age;
    }

    @Override
    public Integer add() {
        return ++count;
    }

}
