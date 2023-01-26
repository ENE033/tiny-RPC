package RPC.instant;

import RPC.core.annotation.ServiceScan;
import RPC.server.RPCServer;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server2 {
    public static void main(String[] args) {
        RPCServer rpcServer = new RPCServer("localhost", 4556);
        rpcServer.run();
    }
}