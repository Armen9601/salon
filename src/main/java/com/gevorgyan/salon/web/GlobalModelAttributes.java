package com.gevorgyan.salon.web;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

  @ModelAttribute("currentPath")
  public String currentPath(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return (uri == null || uri.isBlank()) ? "/" : uri;
  }

  @ModelAttribute("currentUrlNoLang")
  public String currentUrlNoLang(HttpServletRequest request) {
    String path = currentPath(request);
    String query = buildQueryWithoutLang(request.getParameterMap());
    return query.isBlank() ? path : (path + "?" + query);
  }

  private static String buildQueryWithoutLang(Map<String, String[]> params) {
    if (params == null || params.isEmpty()) return "";
    List<String> pairs = new ArrayList<>();
    for (var e : params.entrySet()) {
      String key = e.getKey();
      if (key == null) continue;
      if ("lang".equalsIgnoreCase(key)) continue;
      String[] values = e.getValue();
      if (values == null || values.length == 0) {
        pairs.add(enc(key) + "=");
      } else {
        for (String v : values) {
          pairs.add(enc(key) + "=" + enc(v == null ? "" : v));
        }
      }
    }
    return String.join("&", pairs);
  }

  private static String enc(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8);
  }
}

