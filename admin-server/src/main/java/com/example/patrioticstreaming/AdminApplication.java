package com.example.patrioticstreaming;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableAdminServer
public class AdminApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(AdminApplication.class, args);
    }
}
