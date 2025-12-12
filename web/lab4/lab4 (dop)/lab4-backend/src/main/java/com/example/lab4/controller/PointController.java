package com.example.lab4.controller;

import com.example.lab4.dto.CheckResponseDto;
import com.example.lab4.dto.PointDto;
import com.example.lab4.entity.Point;
import com.example.lab4.service.PointService;
import com.example.lab4.util.JwtUtil;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("/points")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointController {

    @EJB
    private PointService pointService;

    @POST
    @Path("/check")
    public Response checkPoint(PointDto dto, @Context HttpHeaders headers) {
        String username = getUsername(headers);
        if (username == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        try {
            CheckResponseDto response = pointService.processPoint(dto, username);

            if (!response.isSmtVerified()) {
                return Response.status(500).entity(response).build();
            }

            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    public Response getPoints(@Context HttpHeaders headers) {
        String username = getUsername(headers);
        if (username == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        List<Point> points = pointService.getUserPoints(username);
        return Response.ok(points).build();
    }

    private String getUsername(HttpHeaders headers) {
        try {
            String auth = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (auth == null || !auth.startsWith("Bearer ")) return null;
            return JwtUtil.validate(auth.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}