package com.atimera.api.services.impl;

import com.atimera.api.domain.Server;
import com.atimera.api.repositories.ServerRepository;
import com.atimera.api.services.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import static com.atimera.api.enumeration.Status.SERVER_DOWN;
import static com.atimera.api.enumeration.Status.SERVER_UP;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepository serverRepository;

    @Override
    public Server create(Server server) {
        log.info("Saving new server: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepository.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging new IP: {}", ipAddress);
        Server server = serverRepository.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? SERVER_UP : SERVER_DOWN);
        serverRepository.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Listing servers Limit: {}", limit);
        return serverRepository.findAll(PageRequest.of(0, limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by id: {}", id);
        return serverRepository.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server: {}", server.getName());
        return serverRepository.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by ID: {}", id);
        serverRepository.deleteById(id);
        return Boolean.TRUE;
    }

    //====== Private Methods =========//

    private String setServerImageUrl() {
        String[] imageNames = {"server1.jpg","server2.jpg","server3.jpg","server4.jpg","server5.jpg"};
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/server/image/" + imageNames[new Random().nextInt(5)]).toUriString();
    }

}
