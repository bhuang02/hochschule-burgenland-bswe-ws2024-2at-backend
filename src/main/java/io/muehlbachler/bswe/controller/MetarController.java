package io.muehlbachler.bswe.controller;

import io.muehlbachler.bswe.error.ApiException;
import io.muehlbachler.bswe.service.AviationService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to expose METAR endpoints.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/metar")
@CrossOrigin
public class MetarController {
  @Autowired
  private final AviationService aviationService;

  /**
   * Returns the METAR report for an airport.
   *
   * @param icao the ICAO airport code
   * @return the METAR report
   */
  @GetMapping("/{icao}")
  public ResponseEntity<Map<String, Object>> get(@PathVariable final String icao) {
    try {
      return ResponseEntity.ok(aviationService.getMetar(icao));
    } catch (final ApiException e) {
      return new ResponseEntity<>(e.getHttpStatus());
    }
  }
}
