package RPC.core;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

public class ServiceRegistry {

    private static final NamingService NAMING_SERVICE;

    static {
        try {
            NAMING_SERVICE = NamingFactory.createNamingService("1.12.233.55:8848");
        } catch (NacosException e) {
            throw new RuntimeException("注册中心初始化时出现异常:" + e);
        }
    }

    public static void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NAMING_SERVICE.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public static InetSocketAddress getServiceAddress(String serviceName) {
        List<Instance> allInstances;
        try {
            allInstances = NAMING_SERVICE.getAllInstances(serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("获取实例出现异常", e);
        }
        // todo 负载均衡
        Instance instance = allInstances.get(0);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }

}
