package io.muehlbachler.bswe.service;

import io.muehlbachler.bswe.model.location.Coordinates;
import io.muehlbachler.bswe.service.model.nearestairport.NearestAirportResultStation;

import io.muehlbachler.bswe.error.ApiException;
import java.util.Map;

/**
 * A service to handle all aviation related actions.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface AviationService {
  /**
   * Returns the nearest airport to the given coordinates.
   *
   * @param coordinates the coordinates to search for the nearest airport
   * @return the nearest airport to the given coordinates
   */
  NearestAirportResultStation getNearestAirport(Coordinates coordinates);

  /**
   * Returns the METAR report for the given ICAO airport code.
   *
   * @param icao the ICAO airport code
   * @return the METAR report
   * @throws ApiException when the METAR request fails
   */
  Map<String, Object> getMetar(String icao) throws ApiException;
}
