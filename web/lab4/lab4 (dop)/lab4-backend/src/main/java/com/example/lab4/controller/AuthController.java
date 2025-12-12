package com.example.lab4.controller;

import com.example.lab4.dto.CredentialsDto;
import com.example.lab4.service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @EJB
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(CredentialsDto creds) {
        boolean success = userService.register(creds.getUsername(), creds.getPassword());
        if (success) {
            return Response.ok("{\"message\":\"Registered\"}").build();
        } else {
            return Response.status(400).entity("{\"error\":\"User already exists\"}").build();
        }
    }

    @POST
    @Path("/login")
    public Response login(CredentialsDto creds) {
        String token = userService.login(creds.getUsername(), creds.getPassword());
        if (token != null) {
            return Response.ok("{\"token\":\"" + token + "\"}").build();
        } else {
            return Response.status(401).entity("{\"error\":\"Invalid credentials\"}").build();
        }
    }
}