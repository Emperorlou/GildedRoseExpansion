package com.gildedrose.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "specified item could not be found")
public class ItemNotFoundException extends RuntimeException {
}
