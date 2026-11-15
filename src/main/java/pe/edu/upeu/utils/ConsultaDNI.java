package pe.edu.upeu.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pe.edu.upeu.dto.PersonaDto;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class ConsultaDNI {

    private static final String PROPS = "application.properties";
    private static final String DEFAULT_BASE = "https://dniruc.apisperu.com/api/v1";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private String token;

    public ConsultaDNI() {
        this.token = cargarToken();
    }

    private String cargarToken() {
        try (InputStream in = ConsultaDNI.class.getResourceAsStream("/" + PROPS)) {
            if (in == null) return null;
            Properties p = new Properties();
            p.load(in);
            String t = p.getProperty("apisperu.token");
            if (t == null) t = p.getProperty("apisperu.apikey");
            if (t == null) t = p.getProperty("dni.api.token");
            return t != null ? t.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public PersonaDto consultarDNI(String dni) {
        PersonaDto persona = new PersonaDto();
        if (dni == null) return persona;
        String doc = dni.trim();
        if (doc.isEmpty()) return persona;

        // RUC (11) o DNI (8)
        if (doc.length() == 11) {
            PersonaDto r = consultarRuc(doc);
            if (r != null && (r.getRazonSocial() != null || r.getNombre() != null)) return r;
        } else {
            PersonaDto d = consultarSoloDni(doc);
            if (d != null && (d.getNombre() != null || d.getDni() != null)) return d;
            // por si el usuario ingresó algo distinto, intentar como RUC no aplica; fallback
        }

        // si no trajo nada, devolver con documento seteado para no perderlo
        if (persona.getDni() == null) persona.setDni(doc);
        if (persona.getDniRuc() == null) persona.setDniRuc(doc);
        return persona;
    }

    public PersonaDto consultarSoloDni(String dni) {
        if (token == null || token.isBlank()) return null;
        String doc = dni == null ? "" : dni.trim();
        if (doc.length() != 8) return null;
        String url = DEFAULT_BASE + "/dni/" + doc + "?token=" + token;
        try {
            JsonObject json = getJson(url);
            if (json == null) return null;
            if (json.has("success") && !json.get("success").getAsBoolean()) return null;
            // posibles campos: nombres, apellidoPaterno, apellidoMaterno
            String nombres = str(json, "nombres", "nombre");
            String apPat = str(json, "apellidoPaterno", "apellido_paterno");
            String apMat = str(json, "apellidoMaterno", "apellido_materno");
            String dniResp = str(json, "dni", "numeroDocumento");
            if (dniResp == null || dniResp.isBlank()) dniResp = doc;

            PersonaDto p = new PersonaDto();
            p.setDni(dniResp);
            p.setDniRuc(dniResp);
            p.setNombre(nombres);
            p.setApellidoPaterno(apPat);
            p.setApellidoMaterno(apMat);
            String razon = joinNombre(nombres, apPat, apMat);
            p.setRazonSocial(razon);
            p.setDireccion(str(json, "direccion", "direccionCompleta"));
            // si nombres viene vacío pero hay razonSocial en otra variante
            if ((p.getNombre() == null || p.getNombre().isBlank()) && p.getRazonSocial() != null) {
                p.setNombre(p.getRazonSocial());
            }
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    public PersonaDto consultarRuc(String ruc) {
        if (token == null || token.isBlank()) return null;
        String doc = ruc == null ? "" : ruc.trim();
        if (doc.length() != 11) return null;
        String url = DEFAULT_BASE + "/ruc/" + doc + "?token=" + token;
        try {
            JsonObject json = getJson(url);
            if (json == null) return null;
            if (json.has("success") && !json.get("success").getAsBoolean()) return null;
            String razon = str(json, "razonSocial", "nombre", "nombre_o_razon_social");
            String direccion = str(json, "direccion", "direccionCompleta", "domicilioFiscal");
            String rucResp = str(json, "ruc", "numeroDocumento");
            if (rucResp == null || rucResp.isBlank()) rucResp = doc;

            PersonaDto p = new PersonaDto();
            p.setDni(rucResp);
            p.setDniRuc(rucResp);
            p.setRazonSocial(razon);
            p.setNombre(razon);
            p.setDireccion(direccion);
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    private JsonObject getJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) return null;
        String body = resp.body();
        if (body == null || body.isBlank()) return null;
        return JsonParser.parseString(body).getAsJsonObject();
    }

    private String str(JsonObject json, String... keys) {
        for (String k : keys) {
            try {
                if (json.has(k) && !json.get(k).isJsonNull()) {
                    String v = json.get(k).getAsString();
                    if (v != null && !v.isBlank()) return v.trim();
                }
            } catch (Exception ignored) { }
        }
        return null;
    }

    private String joinNombre(String n, String ap, String am) {
        StringBuilder sb = new StringBuilder();
        if (n != null && !n.isBlank()) sb.append(n.trim());
        if (ap != null && !ap.isBlank()) { if (sb.length() > 0) sb.append(' '); sb.append(ap.trim()); }
        if (am != null && !am.isBlank()) { if (sb.length() > 0) sb.append(' '); sb.append(am.trim()); }
        return sb.length() == 0 ? null : sb.toString();
    }

    public static void main(String[] args) {
        ConsultaDNI c = new ConsultaDNI();
        PersonaDto p = c.consultarDNI("43631919");
        if (p != null) {
            System.out.println(p.getDni() + " | " + p.getNombre() + " | " + p.getApellidoPaterno() + " " + p.getApellidoMaterno() + " | " + p.getRazonSocial());
        } else {
            System.out.println("Sin datos");
        }
    }
}
