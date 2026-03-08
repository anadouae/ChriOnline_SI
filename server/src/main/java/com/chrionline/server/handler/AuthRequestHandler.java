package com.chrionline.server.handler;

import com.chrionline.common.Protocol;
import com.chrionline.server.service.AuthService;

/**
 * Handler Auth – Bloc A (Personne 1).
 * Commandes : REGISTER, LOGIN, LOGOUT.
 */
public class AuthRequestHandler implements RequestHandler {

    private final AuthService authService = new AuthService();

    @Override
    public boolean accepts(String command) {
        return "REGISTER".equals(command) || "LOGIN".equals(command) || "LOGOUT".equals(command);
    }

    @Override
    public String handle(String command, String[] parts) {
        switch (command) {
            case "REGISTER":
                if (parts.length >= 4) return authService.register(parts[1], parts[2], parts[3]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            case "LOGIN":
                if (parts.length >= 3) return authService.login(parts[1], parts[2]);
                return Protocol.ERROR + Protocol.SEPARATOR + "INVALID_DATA";
            case "LOGOUT":
                if (parts.length >= 2) return authService.logout(parts[1]);
                return Protocol.OK;
            default:
                return Protocol.ERROR + Protocol.SEPARATOR + "UNKNOWN_COMMAND";
        }
    }
}
