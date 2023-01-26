package RPC.core.config.client.loadBanlance.impl;

import RPC.core.config.client.loadBanlance.LoadBalanceStrategy;
import RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomByWeightLoadBalance implements LoadBalanceStrategy {
    @Override
    public Instance selectInstance(List<Instance> instances, RequestMessage requestMessage) {
        if (instances == null || instances.size() == 0) {
            return null;
        }
        double total = 0;
        boolean allSame = true;
        for (int i = 0; i < instances.size(); i++) {
            total += instances.get(i).getWeight();
            // 前面全等的情况下，当前实例的权值不等于前一个实例的权值
            if (allSame && i > 0 && instances.get(i).getWeight() != instances.get(i - 1).getWeight()) {
                allSame = false;
            }
        }
        // 全等，退化为随机算法
        if (allSame) {
            return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
        }
        // 随机取一个数
        double v = ThreadLocalRandom.current().nextDouble(total);
        int index = -1;
        // 依次递减
        // 直到出现负数，那么这个实例就是被算法选中的实例
        while (v > 0) {
            index++;
            v -= instances.get(index).getWeight();
        }
        return instances.get(index);
    }
}
