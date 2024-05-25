package mx.unam.fi.distributed.messages.settings;

import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.client.IClient;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Component
@RequiredArgsConstructor
public class HeartBeat implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeartBeat.class);
    private final IClient client;
    private final NodeRepository nodeRepository;

    @Value("${app.server.node_n}")
    private int nodeN;

    private void sendMulticastMessage() {
        nodeRepository.getNodes().forEach((n) -> {
            client.sendMessage(n, new Message(nodeN, "HELLO", LocalDateTime.now()));
        });
    }

    @Override
    public void run() {

        boolean isAlive = true;
        boolean isMaster = false;

        while (isAlive) {
            try {
                Thread.sleep(2000);

                sendMulticastMessage();

                int masterId = nodeRepository.getNodesId().stream().mapToInt(i -> i).max().orElse(-1);

                if (masterId == nodeN) {
                    System.out.println("> Yo soy el master");

                    if (!isMaster) {
                        var newToken = String.format("TOKEN;%d", nodeN);
                        log.info("Hey! Sening a new token!!");
                        System.out.println("Sending the message...");
                        log.info("Node: {}", nodeRepository.getNextNode(nodeN));
                        client.sendMessage(nodeRepository.getNextNode(nodeN), new Message(nodeN, newToken, LocalDateTime.now()));
                    }

                    isMaster = true;
                } else {
                    isMaster = false;
                }
            } catch (InterruptedException e) {
                isAlive = false;
                log.info(e.getMessage());
            }
        }
    }
}
