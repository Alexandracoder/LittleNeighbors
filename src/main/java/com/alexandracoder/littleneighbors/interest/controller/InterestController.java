package com.alexandracoder.littleneighbors.interest.controller;

import com.alexandracoder.littleneighbors.interest.dto.InterestResponseDTO;
import com.alexandracoder.littleneighbors.interest.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Tag(name = "Interests", description = "Endpoints for retrieving available child interests")
public class InterestController {

    private final InterestService interestService;

    @Operation(summary = "Get all available interests",
            description = "Returns a list of all interests (Sports, Arts, etc.) to be used in forms.")
    @GetMapping
    public ResponseEntity<List<InterestResponseDTO>> getAllInterests() {
        return ResponseEntity.ok(interestService.findAll());
    }
}