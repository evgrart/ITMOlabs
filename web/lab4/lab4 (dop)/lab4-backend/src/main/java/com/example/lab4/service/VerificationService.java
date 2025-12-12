package com.example.lab4.service;

import com.example.lab4.dto.CheckResponseDto;
import com.example.lab4.dto.ProofStepDto;
import jakarta.ejb.Stateless;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class VerificationService {

    public CheckResponseDto verify(double x, double y, double r) {
        CheckResponseDto response = new CheckResponseDto();
        List<ProofStepDto> proof = new ArrayList<>();
        boolean isHit = false;

        String formula = "";
        String areaDescription = "";
        
        if (x <= 0 && y >= 0) {
            areaDescription = "Sector (Top-Left)";
            formula = "x^2 + y^2 <= r^2";
            isHit = (x * x + y * y <= r * r); 
        } else if (x <= 0 && y <= 0) {
            areaDescription = "Rectangle (Bottom-Left)";
            formula = "(x >= -r) && (y >= -r/2)";
            isHit = (x >= -r && y >= -r / 2.0);
        } else if (x >= 0 && y <= 0) {
            areaDescription = "Triangle (Bottom-Right)";
            formula = "y >= x - r/2";
            isHit = (y >= x - r / 2.0);
        } else {
            areaDescription = "Empty Area (Top-Right)";
            formula = "False";
            isHit = false;
        }

        ProofStepDto step1 = new ProofStepDto("SUBSTITUTION", formula, "Applying values...");
        Map<String, Double> subs = new HashMap<>();
        subs.put("x", x);
        subs.put("y", y);
        subs.put("r", r);
        step1.setSubstitution(subs);
        proof.add(step1);

        String evalStr = formula
                .replace("x", String.format(Locale.US, "(%.2f)", x))
                .replace("y", String.format(Locale.US, "(%.2f)", y))
                .replace("r", String.format(Locale.US, "(%.2f)", r));
        
        proof.add(new ProofStepDto("EVALUATION", evalStr, isHit ? "True" : "False"));

        proof.add(new ProofStepDto("INEQUALITY_CHECK", areaDescription, isHit ? "Inside" : "Outside"));

        response.setInside(isHit);
        response.setProof(proof);


        try {
            boolean z3Result = runZ3Check(x, y, r);
            
            response.setSmtVerified(z3Result == isHit);
            
            proof.add(new ProofStepDto("SMT_VERIFICATION", "Z3 Solver Check", response.isSmtVerified() ? "CONFIRMED" : "DISCREPANCY"));
            
        } catch (Exception e) {
            System.err.println("Z3 Error: " + e.getMessage());
            response.setSmtVerified(false);
            proof.add(new ProofStepDto("SMT_VERIFICATION", "Z3 Execution", "ERROR: " + e.getMessage()));
        }

        return response;
    }


    private boolean runZ3Check(double x, double y, double r) throws Exception {
        if (r <= 0) return false; 


        String smtQuery = String.format(Locale.US,
                "(declare-const x Real)\n" +
                "(declare-const y Real)\n" +
                "(declare-const r Real)\n" +
                "(assert (= x %f))\n" +
                "(assert (= y %f))\n" +
                "(assert (= r %f))\n" +
                "(assert (or \n" +
                "  (and (<= x 0) (>= y 0) (<= (+ (* x x) (* y y)) (* r r))) \n" +
                "  (and (<= x 0) (<= y 0) (>= x (- r)) (>= y (/ (- r) 2))) \n" +
                "  (and (>= x 0) (<= y 0) (>= y (- x (/ r 2)))) \n" +
                "))\n" +
                "(check-sat)\n", x, y, r);

        ProcessBuilder pb = new ProcessBuilder("z3", "-in");
        Process p = pb.start();

        try (OutputStream os = p.getOutputStream()) {
            os.write(smtQuery.getBytes());
            os.flush();
        }

        if (!p.waitFor(500, TimeUnit.MILLISECONDS)) {
            p.destroy();
            throw new RuntimeException("Timeout: Z3 verification took too long");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine(); 
        
        return "sat".equals(line); // satisfiable
    }
}