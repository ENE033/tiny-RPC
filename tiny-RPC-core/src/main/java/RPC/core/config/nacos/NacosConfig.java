package RPC.core.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nacos配置中心，获取配置中心的配置信息，监听配置信息的变动
 */
public class NacosConfig {
    public static String SERIALIZE_TYPE = "serializer";
    public static String LOADBALANCE_TYPE = "loadBalance";
    public static String WRITEBACK_TYPE = "writeBack";
    public static String PROXY_MODE = "proxyMode";

    /**
     * 注册中心的地址
     */
    protected volatile String nacosRegistryAddress;

    /**
     * 配置中心的地址
     */
    protected volatile String nacosConfigAddress;

    /**
     * 配置中心的dataId
     */
    protected volatile String nacosConfigDataId;

    /**
     * 配置中心的group
     */
    protected volatile String nacosConfigGroup;

    private final Map<String, String> properties = new ConcurrentHashMap<>();

    protected volatile ConfigService configService;

    public NacosConfig() {
    }

    /**
     * 延迟初始化，为了整合spring
     */
    public void init() {
        try {
            configService = NacosFactory.createConfigService(nacosConfigAddress);
            String configs = configService.getConfig(nacosConfigDataId, nacosConfigGroup, 10000);
            for (String config : configs.split("\n")) {
                String[] entry = config.split("=");
                getProperties().put(entry[0], entry[1]);
            }
            // 注册监听器
            configService.addListener(nacosConfigDataId, nacosConfigGroup, new PropertiesListener() {
                @Override
                public void innerReceive(Properties properties) {
                    getProperties().clear();
                    properties.forEach((k, v) -> getProperties().put((String) k, (String) v));
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public String getConfig(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        throw new RuntimeException("该属性不存在");
    }

    public String getConfigEnableAbleRetry(String key, int time) {
        if (time == 0) {
            throw new RuntimeException("该属性不存在");
        }
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getConfigEnableAbleRetry(key, time - 1);
    }


    public Integer getConfigAsInt(String key) {
        String value = getConfigEnableAbleRetry(key, 3);
        return Integer.parseInt(value);
    }

    public String getNacosRegistryAddress() {
        return nacosRegistryAddress;
    }

    public void setNacosRegistryAddress(String nacosRegistryAddress) {
        this.nacosRegistryAddress = nacosRegistryAddress;
    }

    public String getNacosConfigAddress() {
        return nacosConfigAddress;
    }

    public void setNacosConfigAddress(String nacosConfigAddress) {
        this.nacosConfigAddress = nacosConfigAddress;
    }

    public String getNacosConfigDataId() {
        return nacosConfigDataId;
    }

    public void setNacosConfigDataId(String nacosConfigDataId) {
        this.nacosConfigDataId = nacosConfigDataId;
    }

    public String getNacosConfigGroup() {
        return nacosConfigGroup;
    }

    public void setNacosConfigGroup(String nacosConfigGroup) {
        this.nacosConfigGroup = nacosConfigGroup;
    }

    public Map<String, String> getProperties() {
        return properties;
    }


}
