package com.example.lab3;

import com.example.lab3.service.AreaHitService;
import com.example.lab3.util.Messages;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        try {
            double x = args.length > 0 ? Double.parseDouble(args[0]) : 0.0;
            double y = args.length > 1 ? Double.parseDouble(args[1]) : 0.0;
            double r = args.length > 2 ? Double.parseDouble(args[2]) : 1.5;

            boolean hit = AreaHitService.isHit(x, y, r);
            String result = hit ? Messages.get("app.hit.result") : Messages.get("app.miss.result");
            System.out.printf(Messages.get("app.output.template"), x, y, r, result);
        } catch (NumberFormatException e) {
            System.err.println(Messages.get("app.invalid.args"));
            System.exit(1);
        }
    }
}
