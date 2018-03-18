package io.toro.pairprogramming.services;

import java.net.URI;
import org.springframework.stereotype.Service;

public class UrlService {

  public static final String DOMAIN = URI.create("http://localhost:8000/").toString();
}
